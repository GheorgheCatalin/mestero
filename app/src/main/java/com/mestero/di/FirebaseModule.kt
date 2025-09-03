package com.mestero.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Tells Hilt how to create FirebaseAuth and FirebaseFirestore

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // Singleton scope - only one instance of each throughout the entire app
    @Provides //@Provides - Because these are concrete objects, not interfaces
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
} 