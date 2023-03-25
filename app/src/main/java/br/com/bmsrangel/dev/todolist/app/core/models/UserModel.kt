package br.com.bmsrangel.dev.todolist.app.core.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(val uid: String, val name: String?, val email: String, val photoUrl: String?)