package com.mestero.ui.dashboard.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mestero.data.UserType
import com.mestero.data.models.ListingModel
import com.mestero.network.auth.AccountService
import com.mestero.network.firestore.FirestoreQueryParams
import com.mestero.network.firestore.FirestoreRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class HomeViewModelTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    @Test
    fun `loadMyPosts fetches provider posts`() {
        val firestoreRepo = mockk<FirestoreRepository>()
        val accountService = mockk<AccountService>()

        every { accountService.currentUserId } returns "uid123"
        every { accountService.fetchUserData(onResult = any()) } answers {
            val cb = arg<(UserType, String) -> Unit>(0)
            cb(UserType.PROVIDER, "John")
        }

        val snapshot = mockk<QuerySnapshot>()
        val doc = mockDoc("1", providerId = "uid123")
        every { snapshot.documents } returns listOf(doc)
        coEvery { firestoreRepo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns snapshot

        val vm = HomeViewModel(firestoreRepo, accountService)

        // After init, it should load data and then set myPosts
        val posts = vm.myPosts.getOrAwait()
        assertEquals(1, posts.size)
        assertEquals("1", posts.first().id)
    }

    private fun mockDoc(id: String, providerId: String): DocumentSnapshot {
        val data = mapOf(
            "title" to "Test",
            "description" to "desc",
            "category" to "Home",
            "subcategory" to "Plumbing",
            "tags" to emptyList<String>(),
            "providerId" to providerId,
            "active" to true
        )
        val doc = mockk<DocumentSnapshot>()
        every { doc.data } returns data
        every { doc.id } returns id
        return doc
    }
}

// Minimal LiveData await helper for tests
private fun <T> androidx.lifecycle.LiveData<T>.getOrAwait(): T {
    var value: T? = null
    val latch = java.util.concurrent.CountDownLatch(1)
    val obs = object : androidx.lifecycle.Observer<T> {
        override fun onChanged(t: T) {
            value = t
            latch.countDown()
            this@getOrAwait.removeObserver(this)
        }
    }
    this.observeForever(obs)
    latch.await()
    return value as T
}



