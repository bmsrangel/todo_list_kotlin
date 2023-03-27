package br.com.bmsrangel.dev.todolist.app.core.viewmodels

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth.AuthRepository
import br.com.bmsrangel.dev.todolist.app.core.services.user.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (private val authRepository: AuthRepository, private val userService: UserService): ViewModel() {
    private var userLiveData: MutableLiveData<Result<UserModel?>> = MutableLiveData<Result<UserModel?>>()
    private val loginDTOLiveData: MutableLiveData<LoginDTO> = MutableLiveData<LoginDTO>()
    private val registerDTOLiveData: MutableLiveData<RegisterDTO> = MutableLiveData<RegisterDTO>()
    private val accountIdTokenLiveData: MutableLiveData<String> = MutableLiveData<String>()

    fun getUser(): LiveData<Result<UserModel?>> {
        return userLiveData
    }

    fun getUserFromLocalStorage() {
        val user = userService.getUser()
        this.userLiveData
    }

    fun login(loginDTO: LoginDTO) {
        loginDTOLiveData.postValue(loginDTO)

        userLiveData = Transformations.switchMap(loginDTOLiveData) {
            authRepository.login(it)
        } as MutableLiveData<Result<UserModel?>>
    }

    fun register(registerDTO: RegisterDTO) {
        registerDTOLiveData.postValue(registerDTO)

        userLiveData = Transformations.switchMap(registerDTOLiveData) {
            authRepository.register(it)
        } as MutableLiveData<Result<UserModel?>>
    }

    fun loginWithGoogle(accountIdToken: String?) {
        this.accountIdTokenLiveData.postValue(accountIdToken)
        userLiveData = Transformations.switchMap(this.accountIdTokenLiveData) {
            authRepository.signInWithGoogle(it)
        } as MutableLiveData<Result<UserModel?>>
    }
}