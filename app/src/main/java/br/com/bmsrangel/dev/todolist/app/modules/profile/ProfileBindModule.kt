package br.com.bmsrangel.dev.todolist.app.modules.profile

import br.com.bmsrangel.dev.todolist.app.core.repositories.storage.FirebaseStorageRepositoryImpl
import br.com.bmsrangel.dev.todolist.app.core.repositories.storage.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileBindModule {
    @Binds
    abstract fun bindStorageRepository(storageRepositoryImpl: FirebaseStorageRepositoryImpl): StorageRepository
}