package br.com.bmsrangel.dev.todolist.app.modules.main

import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks.FirebaseTasksRepositoryImpl
import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MainBindModule {
    @Binds
    abstract fun bindTasksRepository(tasksRepositoryImpl: FirebaseTasksRepositoryImpl): TasksRepository
}