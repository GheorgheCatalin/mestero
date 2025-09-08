package com.mestero.ui.dashboard.addListing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FieldValue
import com.mestero.data.models.ListingModel
import com.mestero.data.models.PricingModel
import com.mestero.data.models.PricingUnit
import com.mestero.network.auth.AccountService
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
class AddListingViewModelTest {
    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun validListing(): ListingModel = ListingModel(
        title = "Title",
        description = "Long enough description",
        category = "Home",
        subcategory = "Plumbing",
        pricingModel = PricingModel.createFixed(50.0, PricingUnit.TOTAL)
    )

    @Test
    fun `createListing success emits document id`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val account = mockk<AccountService>()
        every { account.currentUserId } returns "uid"
        coEvery { repo.addDocument(any(), any()) } returns mockk { every { id } returns "docId" }

        val vm = AddListingViewModel(repo, account)
        vm.createListing(validListing())
        testDispatcher.scheduler.advanceUntilIdle()

        val result = vm.createListingResult.value
        Assert.assertTrue(result?.isSuccess == true)
        Assert.assertEquals("docId", result?.getOrNull())
    }

    @Test
    fun `createListing fails on unauthenticated user`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val account = mockk<AccountService>()
        every { account.currentUserId } returns ""

        val vm = AddListingViewModel(repo, account)
        vm.createListing(validListing())
        testDispatcher.scheduler.advanceUntilIdle()

        val result = vm.createListingResult.value
        Assert.assertTrue(result?.isFailure == true)
    }

    @Test
    fun `createListing fails on validation errors`() = runTest(testDispatcher) {
        val repo = mockk<FirestoreRepository>()
        val account = mockk<AccountService>()
        every { account.currentUserId } returns "uid"

        val vm = AddListingViewModel(repo, account)
        val invalid = validListing().copy(title = "ab")
        vm.createListing(invalid)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = vm.createListingResult.value
        Assert.assertTrue(result?.isFailure == true)
    }

    @Test
    fun `toMap sets createdAt to serverTimestamp when null`() {
        val listing = validListing()
        val map = listing.toMap()
        
        Assert.assertTrue(map["createdAt"] is FieldValue)
        Assert.assertEquals(FieldValue.serverTimestamp(), map["createdAt"])
    }
}


