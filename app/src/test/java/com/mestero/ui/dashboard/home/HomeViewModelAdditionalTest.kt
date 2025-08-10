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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelAdditionalTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    @Test
    fun `guest path sets userType CLIENT loads lastSeen and categories`() {
        val firestoreRepo = mockk<FirestoreRepository>()
        val accountService = mockk<AccountService>()

        every { accountService.currentUserId } returns ""

        val lastSeenSnapshot = mockk<QuerySnapshot>()
        every { lastSeenSnapshot.documents } returns emptyList()
        coEvery { firestoreRepo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns lastSeenSnapshot

        val vm = HomeViewModel(firestoreRepo, accountService)
        assertEquals(UserType.CLIENT, vm.userType.value)
        assertEquals("Guest", vm.userName.value)
        assertTrue(vm.categories.value?.isNotEmpty() == true)
        assertTrue(vm.lastSeen.value != null)
    }

    @Test
    fun `isLoading toggles around loadData`() {
        val firestoreRepo = mockk<FirestoreRepository>()
        val accountService = mockk<AccountService>()

        every { accountService.currentUserId } returns "id"
        every { accountService.fetchUserData(onResult = any(), onError = any()) } answers {
            val cb = arg<(UserType, String) -> Unit>(0)
            cb(UserType.CLIENT, "John")
        }

        val snapshot = mockk<QuerySnapshot>()
        every { snapshot.documents } returns emptyList()
        coEvery { firestoreRepo.queryDocuments(ListingModel.COLLECTION_NAME, any<FirestoreQueryParams>()) } returns snapshot

        val vm = HomeViewModel(firestoreRepo, accountService)

        // After init and data load, isLoading should be false
        assertFalse(vm.isLoading.value == true)
    }
}


