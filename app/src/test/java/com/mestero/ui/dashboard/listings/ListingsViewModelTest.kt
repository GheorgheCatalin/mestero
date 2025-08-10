package com.mestero.ui.dashboard.listings

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
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class ListingsViewModelTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadListings emits Success with items`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val snapshot = mockk<QuerySnapshot>()
        every { snapshot.documents } returns listOf(fakeDoc("id1"))
        coEvery { repo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns snapshot

        val vm = ListingsViewModel(repo)
        vm.loadListings("Home", "Plumbing")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.listingsState.value
        Assert.assertTrue(state is ListingsUiState.Success)
        val list = (state as ListingsUiState.Success).listings
        Assert.assertEquals(1, list.size)
        Assert.assertEquals("id1", list.first().id)
    }

    @Test
    fun `loadListings emits Empty when no docs`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val snapshot = mockk<QuerySnapshot>()
        every { snapshot.documents } returns emptyList()
        coEvery { repo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns snapshot

        val vm = ListingsViewModel(repo)
        vm.loadListings("Home", "Plumbing")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.listingsState.value
        Assert.assertTrue(state is ListingsUiState.Empty)
    }

    @Test
    fun `loadListings emits Error on exception`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        coEvery { repo.queryDocuments(any(), any<FirestoreQueryParams>()) } throws RuntimeException("boom")

        val vm = ListingsViewModel(repo)
        vm.loadListings("Home", "Plumbing")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.listingsState.value
        Assert.assertTrue(state is ListingsUiState.Error)
    }

    private fun fakeDoc(id: String): DocumentSnapshot {
        val data = mapOf(
            "title" to "Test",
            "description" to "desc",
            "category" to "Home",
            "subcategory" to "Plumbing",
            "tags" to emptyList<String>(),
            "providerId" to "uid",
            "active" to true
        )
        val doc = mockk<DocumentSnapshot>()
        every { doc.data } returns data
        every { doc.id } returns id
        return doc
    }
}


