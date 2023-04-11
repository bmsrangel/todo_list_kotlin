package br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks.TasksRepository
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.tasks.states.ErrorTasksState
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.tasks.states.SuccessTasksState
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.tasks.states.TasksState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val tasksRepository: TasksRepository): ViewModel() {
    private var tasksLiveData = MutableLiveData<TasksState>()

    fun tasks() = tasksLiveData as LiveData<TasksState>

    fun fetchTasks(userId: String) {
        val result = tasksRepository.getTasksByUserId(userId)
        result.fold({
            tasksLiveData = it.map { taskList -> SuccessTasksState(taskList) } as MutableLiveData<TasksState>
        }, {
            tasksLiveData.value = ErrorTasksState(it.message)
        })
    }

    fun removeSelectedTasks(userId: String, taskId: String) {
        tasksRepository.removeTaskById(userId, taskId)
    }
    fun createTask(userId: String, newTaskDto: NewTaskDto) {
        tasksRepository.createNewTask(userId, newTaskDto)
    }
}