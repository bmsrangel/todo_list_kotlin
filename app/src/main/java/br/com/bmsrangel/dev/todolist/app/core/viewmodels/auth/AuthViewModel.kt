package br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth

import android.net.Uri
import androidx.lifecycle.*
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.ProfileDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import br.com.bmsrangel.dev.todolist.app.core.repositories.auth.AuthRepository
import br.com.bmsrangel.dev.todolist.app.core.services.user.UserService
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (private val authRepository: AuthRepository, private val userService: UserService): ViewModel() {
    private var userLiveData: MutableLiveData<AuthState> = MutableLiveData<AuthState>()
    var userModel: UserModel? = null

    fun getUser(): LiveData<AuthState> {
        return userLiveData
    }

    fun getUserFromLocalStorage() {
        val user = userService.getUser()
        if (user != null) {
            userLiveData.postValue(SuccessAuthState(user))
            userModel = user
        } else {
            userLiveData.postValue(UnauthenticatedAuthState())
        }

    }

    fun login(loginDTO: LoginDTO) {
        userLiveData.postValue(LoadingAuthState())

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.login(loginDTO)
            }
            result.fold({
                userLiveData.postValue(SuccessAuthState(it))
            }, {
                userLiveData.postValue(ErrorAuthState(it.message))
                userLiveData.postValue(InitialState())
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

    fun updatePassword(newPassword: String) {
        userLiveData.value = LoadingAuthState()
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.updatePassword(newPassword)
            }
            result.fold({
                userLiveData.value = PasswordUpdateSuccessAuthState()
            }, {
                userLiveData.value = ErrorAuthState(it.message)
                userLiveData.value = InitialState()
            })
        }
    }

    fun sendForgotPasswordEmail(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
        }
    }

    fun updateProfile(profileDTO: ProfileDTO) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authRepository.updateUserProfile(profileDTO)
            }
            result.fold({
                val updatedUser = userService.getUser()
                userLiveData.postValue(SuccessAuthState(updatedUser!!))
            }, {
                println(it.message)
            })
        }
    }
}