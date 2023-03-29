package br.com.bmsrangel.dev.todolist.app.modules.main.states

import androidx.lifecycle.LiveData
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel

class SuccessTasksState(val tasksLiveData: LiveData<Array<TaskModel>>): TasksState