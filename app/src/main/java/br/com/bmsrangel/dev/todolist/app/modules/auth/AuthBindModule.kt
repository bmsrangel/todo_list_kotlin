package br.com.bmsrangel.dev.todolist.app.modules.auth

import br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth.AuthRepository
import br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth.FirebaseAuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindModule {
    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: FirebaseAuthRepositoryImpl) : AuthRepository
}