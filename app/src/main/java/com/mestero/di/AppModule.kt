package com.mestero.di


import com.mestero.network.auth.AccountService
import com.mestero.network.auth.AccountServiceImpl
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Hilt Module
// Connects interfaces to implementations - Links AccountService → AccountServiceImpl
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Binds the [AccountServiceImpl] to the [AccountService] interface.
     *
     * This function is used by Dagger's Hilt dependency injection framework to provide
     * an instance of [AccountService] whenever it's requested. By using `@Binds`,
     * we tell Dagger to use the provided `impl` (which is an instance of `AccountServiceImpl`)
     * whenever an `AccountService` is needed. The `@Singleton` annotation ensures that
     * only one instance of `AccountService` (and therefore `AccountServiceImpl`) is created
     * throughout the application's lifecycle.
     *
     * @param impl The concrete implementation of AccountService, which is AccountServiceImpl.
     * @return An instance of AccountService.
     */

    // Bind implementation class to the interface for Hilt to use.
    @Binds  // @Binds - Because these are interface → implementation mappings
    @Singleton  // Singleton scope - Only one instance of each service
    abstract fun bindAccountService(
        impl: AccountServiceImpl
    ): AccountService


    // Tell Hilt how to create FirestoreRepository. You can do it by adding a Hilt @Provides or @Binds method in a module.
    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(
        impl: FirestoreRepositoryImpl
    ): FirestoreRepository
}
