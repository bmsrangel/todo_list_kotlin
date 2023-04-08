package br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.single_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingleTaskViewModel @Inject constructor(private val tasksRepository: TasksRepository): ViewModel() {
    fun createTask(newTaskDto: NewTaskDto, userId: String) {
        tasksRepository.createNewTask(userId, newTaskDto)
    }

    fun updateTask(task: TaskModel, userId: String) {
        tasksRepository.updateTask(userId, task)
    }
}