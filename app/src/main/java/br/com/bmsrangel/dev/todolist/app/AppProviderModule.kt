package br.com.bmsrangel.dev.todolist.app

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProviderModule {
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics
}