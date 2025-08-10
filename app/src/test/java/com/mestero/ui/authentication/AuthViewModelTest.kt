package com.mestero.ui.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mestero.data.models.UserModel
import com.mestero.network.auth.AccountService
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
class AuthViewModelTest {
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

    @Test
    fun `signIn sets Success on success`() = runTest(testDispatcher) {
        val svc = mockk<AccountService>()
        coEvery { svc.signInUsingEmailAndPassword(any(), any()) } returns Unit

        val vm = AuthViewModel(svc)
        vm.signIn("a@b.com", "pw")
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(vm.authState.value is AuthResult.Success)
    }

    @Test
    fun `signIn sets Error on failure`() = runTest(testDispatcher) {
        val svc = mockk<AccountService>()
        coEvery { svc.signInUsingEmailAndPassword(any(), any()) } throws RuntimeException("boom")

        val vm = AuthViewModel(svc)
        vm.signIn("a@b.com", "pw")
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(vm.authState.value is AuthResult.Error)
    }

    @Test
    fun `register sets Success on success`() = runTest(testDispatcher) {
        val svc = mockk<AccountService>()
        coEvery { svc.signUpUsingEmailAndPassword(any(), any()) } returns Unit
        every { svc.currentUserId } returns "uid"
        coEvery { svc.saveUserData(any()) } returns Unit

        val vm = AuthViewModel(svc)
        vm.registerUsingEmailAndPassword(UserModel(email = "a@b.com"), "pw")
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(vm.authState.value is AuthResult.Success)
    }

    @Test
    fun `register sets Error on exception`() = runTest(testDispatcher) {
        val svc = mockk<AccountService>()
        coEvery { svc.signUpUsingEmailAndPassword(any(), any()) } throws RuntimeException("boom")

        val vm = AuthViewModel(svc)
        vm.registerUsingEmailAndPassword(UserModel(email = "a@b.com"), "pw")
        testDispatcher.scheduler.advanceUntilIdle()
        Assert.assertTrue(vm.authState.value is AuthResult.Error)
    }
}


