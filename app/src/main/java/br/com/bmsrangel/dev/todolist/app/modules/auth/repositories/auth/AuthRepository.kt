package br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth

import androidx.lifecycle.LiveData
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface AuthRepository {
    suspend fun login(loginDTO: LoginDTO): Result<UserModel>
    suspend fun register(registerDTO: RegisterDTO): Result<UserModel>
    suspend fun resetPassword(email: String): Void
    suspend fun signInWithGoogle(accountIdToken: String): Result<UserModel>
    fun signOut()
}