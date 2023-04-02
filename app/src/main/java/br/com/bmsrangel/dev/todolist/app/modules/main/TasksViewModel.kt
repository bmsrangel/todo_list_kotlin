package br.com.bmsrangel.dev.todolist.app.modules.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks.TasksRepository
import br.com.bmsrangel.dev.todolist.app.modules.main.states.ErrorTasksState
import br.com.bmsrangel.dev.todolist.app.modules.main.states.SuccessTasksState
import br.com.bmsrangel.dev.todolist.app.modules.main.states.TasksState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val tasksRepository: TasksRepository): ViewModel() {
    private var tasksLiveData = MutableLiveData<TasksState>()

    fun tasks() = tasksLiveData as LiveData<TasksState>

    fun fetchTasks(userId: String) {
        // TODO: check if it's possible to refactor this to work with a single LiveData instead of nested
        // Transforms.switchMap, perhaps?
        val result = tasksRepository.getTasksByUserId(userId)
        result.fold({
            tasksLiveData = it.map { taskList -> SuccessTasksState(taskList) } as MutableLiveData<TasksState>
        }, {
            tasksLiveData.value = ErrorTasksState(it.message)
        })
    }

    fun removeSelectedTasks(userId: String, taskIdList: Array<String>) {
        tasksRepository.removeSelectedTasks(userId, taskIdList)
    }
    fun createTask(userId: String, newTaskDto: NewTaskDto) {
        tasksRepository.createNewTask(userId, newTaskDto)
    }
}