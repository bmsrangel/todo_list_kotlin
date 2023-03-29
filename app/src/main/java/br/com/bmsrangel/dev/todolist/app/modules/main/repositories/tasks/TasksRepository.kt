package br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks

import androidx.lifecycle.LiveData
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel

interface TasksRepository {
    fun getTasksByUserId(userId: String): Result<LiveData<Array<TaskModel>>>
}