package br.com.bmsrangel.dev.todolist.app.modules.main.models
import kotlinx.serialization.Serializable

@Serializable
data class TaskModel(val id: String, val description: String, val dueDate: String)
