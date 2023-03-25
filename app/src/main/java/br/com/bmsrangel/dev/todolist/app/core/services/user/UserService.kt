package br.com.bmsrangel.dev.todolist.app.core.services.user

import br.com.bmsrangel.dev.todolist.app.core.models.UserModel

interface UserService {
    fun getUser(): UserModel?
}