package br.com.bmsrangel.dev.todolist.app.core.viewmodels

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.bmsrangel.dev.todolist.app.TodoList
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth.AuthRepository
import br.com.bmsrangel.dev.todolist.app.core.services.user.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (private val authRepository: AuthRepository, private val userService: UserService): ViewModel() {
    private var user: MutableLiveData<Result<UserModel?>> = MutableLiveData<Result<UserModel?>>()
    private val loginData: MutableLiveData<LoginDTO> = MutableLiveData<LoginDTO>()
    private val registerData: MutableLiveData<RegisterDTO> = MutableLiveData<RegisterDTO>()

    fun getUser(): LiveData<Result<UserModel?>> {
        return user
    }

    fun getUserFromLocalStorage() {
        val user = userService.getUser()
        this.user
    }

    fun login(loginDTO: LoginDTO) {
        loginData.postValue(loginDTO)

        user = Transformations.switchMap(loginData) {
            authRepository.login(it)
        } as MutableLiveData<Result<UserModel?>>
    }

    fun register(registerDTO: RegisterDTO) {
        registerData.postValue(registerDTO)

        user = Transformations.switchMap(registerData) {
            authRepository.register(it)
        } as MutableLiveData<Result<UserModel?>>
    }
}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory @Inject constructor(
    private val authRepository: AuthRepository,
    private val userService: UserService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, userService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}