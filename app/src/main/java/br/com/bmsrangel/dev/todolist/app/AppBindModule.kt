package br.com.bmsrangel.dev.todolist.app

import br.com.bmsrangel.dev.todolist.app.core.services.permissions.AndroidPermissionsServiceImpl
import br.com.bmsrangel.dev.todolist.app.core.services.permissions.PermissionsService
import br.com.bmsrangel.dev.todolist.app.core.services.user.FirebaseAuthUserServiceImpl
import br.com.bmsrangel.dev.todolist.app.core.services.user.UserService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule {
    @Binds
    abstract fun bindUserService(userServiceImpl: FirebaseAuthUserServiceImpl): UserService

    @Binds
    abstract fun bindPermissionsService(permissionsService: AndroidPermissionsServiceImpl): PermissionsService
}