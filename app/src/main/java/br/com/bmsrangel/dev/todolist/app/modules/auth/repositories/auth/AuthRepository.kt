package br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth

import androidx.lifecycle.LiveData
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel

interface AuthRepository {
    fun login(loginDTO: LoginDTO): LiveData<Result<UserModel?>>
    fun register(registerDTO: RegisterDTO): LiveData<Result<UserModel?>>
    fun resetPassword(email: String): Void
}