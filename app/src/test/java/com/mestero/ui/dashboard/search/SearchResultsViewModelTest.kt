package com.mestero.ui.dashboard.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mestero.data.models.ListingModel
import com.mestero.network.firestore.FirestoreQueryParams
import com.mestero.network.firestore.FirestoreRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResultsViewModelTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchListings filters by title and tags`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val snapshot = mockk<QuerySnapshot>()

        val doc1 = fakeDoc("1", title = "Plumber available", tags = listOf("water", "fix"))
        val doc2 = fakeDoc("2", title = "Electrician", tags = listOf("wiring"))
        val doc3 = fakeDoc("3", title = "Gardener", tags = listOf("plants"))

        every { snapshot.documents } returns listOf(doc1, doc2, doc3)
        coEvery { repo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns snapshot

        val vm = SearchResultsViewModel(repo)

        vm.searchListings("plumb")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.searchState.value
        assertTrue(state is SearchResultsUiState.Success)
        val listings = (state as SearchResultsUiState.Success).listings
        assertTrue(listings.size == 1 && listings.first().title.contains("Plumber"))

        vm.searchListings("plants")
        testDispatcher.scheduler.advanceUntilIdle()
        val state2 = vm.searchState.value
        assertTrue(state2 is SearchResultsUiState.Success)
        val listings2 = (state2 as SearchResultsUiState.Success).listings
        assertTrue(listings2.size == 1 && listings2.first().title == "Gardener")
    }

    @Test
    fun `searchListings emits Empty on blank query`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val vm = SearchResultsViewModel(repo)

        vm.searchListings("  ")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.searchState.value
        assertTrue(state is SearchResultsUiState.Empty)
    }

    @Test
    fun `searchListings emits Error on repository exception`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        coEvery { repo.queryDocuments(any(), any<FirestoreQueryParams>()) } throws RuntimeException("boom")

        val vm = SearchResultsViewModel(repo)
        vm.searchListings("test")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.searchState.value
        assertTrue(state is SearchResultsUiState.Error)
    }

    @Test
    fun `searchInSpecificCategory emits Empty when no matches`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val snapshot = mockk<QuerySnapshot>()
        every { snapshot.documents } returns emptyList()
        coEvery { repo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns snapshot

        val vm = SearchResultsViewModel(repo)
        vm.searchInSpecificCategory("abc", categoryId = "Home")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.searchState.value
        assertTrue(state is SearchResultsUiState.Empty)
    }

    private fun fakeDoc(
        id: String,
        title: String,
        description: String = "desc",
        category: String = "Home",
        subcategory: String = "Plumbing",
        tags: List<String> = emptyList()
    ): DocumentSnapshot {
        val data = mapOf(
            "title" to title,
            "description" to description,
            "category" to category,
            "subcategory" to subcategory,
            "tags" to tags,
            "providerId" to "uid",
            "active" to true
        )
        val doc = mockk<DocumentSnapshot>()
        every { doc.data } returns data
        every { doc.id } returns id
        return doc
    }
}



