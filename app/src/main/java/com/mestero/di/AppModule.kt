package com.mestero.di


import com.mestero.network.auth.AccountService
import com.mestero.network.auth.AccountServiceImpl
import com.mestero.network.firestore.FirestoreRepository
import com.mestero.network.firestore.FirestoreRepositoryImpl
import com.mestero.network.messaging.MessagingRepository
import com.mestero.network.messaging.MessagingRepositoryImpl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Hilt Module
// Connects interfaces to implementations
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAccountService(
        impl: AccountServiceImpl
    ): AccountService


    @Binds
    @Singleton
    abstract fun bindFirestoreRepository(
        impl: FirestoreRepositoryImpl
    ): FirestoreRepository

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(
        impl: MessagingRepositoryImpl
    ): MessagingRepository
}
