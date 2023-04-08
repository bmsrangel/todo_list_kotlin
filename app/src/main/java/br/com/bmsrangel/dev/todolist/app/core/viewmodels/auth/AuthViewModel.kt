package br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth

import androidx.lifecycle.*
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth.AuthRepository
import br.com.bmsrangel.dev.todolist.app.core.services.user.UserService
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (private val authRepository: AuthRepository, private val userService: UserService): ViewModel() {
    private var userLiveData: MutableLiveData<AuthState> = MutableLiveData<AuthState>()

    fun getUser(): LiveData<AuthState> {
        return userLiveData
    }

    fun getUserFromLocalStorage() {
        val user = userService.getUser()
        if (user != null) {
            userLiveData.value = SuccessAuthState(user)
        } else {
            userLiveData.value = UnauthenticatedAuthState()
        }

    }

    fun login(loginDTO: LoginDTO) {
        userLiveData.value = (LoadingAuthState())

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.login(loginDTO)
            }
            result.fold({
                userLiveData.value = (SuccessAuthState(it))
            }, {
                userLiveData.value = ErrorAuthState(it.message)
                userLiveData.value = InitialState()
            })
        }

    }

    fun register(registerDTO: RegisterDTO) {
        userLiveData.value = LoadingAuthState()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.register(registerDTO)
            }
            result.fold({
                userLiveData.value = SuccessAuthState(it)
            }, {
                userLiveData.value = ErrorAuthState(it.message)
                userLiveData.value = InitialState()
            })
        }
    }

    fun loginWithGoogle(accountIdToken: String?) {
        userLiveData.value = LoadingAuthState()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.signInWithGoogle(accountIdToken!!)
            }
            result.fold({
                userLiveData.value = SuccessAuthState(it)
            }, {
                userLiveData.value = ErrorAuthState(it.message)
                userLiveData.value = InitialState()
            })
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}