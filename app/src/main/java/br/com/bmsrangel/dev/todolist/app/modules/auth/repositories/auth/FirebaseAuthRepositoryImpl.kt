package br.com.bmsrangel.dev.todolist.app.modules.auth.repositories.auth

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) : AuthRepository {
    override fun login(loginDTO: LoginDTO): LiveData<Result<UserModel?>> {
        val userResult = MutableLiveData<Result<UserModel?>>()
        try {
            val task = firebaseAuth.signInWithEmailAndPassword(loginDTO.email, loginDTO.password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = it.result.user
                    userResult.postValue(Result.success(UserModel(firebaseUser!!.uid, firebaseUser.displayName, firebaseUser.email!!, firebaseUser.photoUrl.toString())))
                } else {
                    userResult.postValue(Result.failure(java.lang.Exception()))
                }
            }
        } catch (e: java.lang.Exception) {
            userResult.postValue(Result.failure(e))
        }
        return userResult
    }

    override fun register(registerDTO: RegisterDTO): LiveData<Result<UserModel?>> {
        val userResult = MutableLiveData<Result<UserModel?>>()
        try {
            firebaseAuth.createUserWithEmailAndPassword(registerDTO.email, registerDTO.password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = it.result.user
                    val profileUpdates = userProfileChangeRequest {
                        displayName = registerDTO.name
                    }
                    firebaseUser!!.updateProfile(profileUpdates)
                    userResult.postValue(Result.success(UserModel(firebaseUser.uid, firebaseUser.displayName, firebaseUser.email!!, firebaseUser.photoUrl.toString())))
                } else {
                    userResult.postValue(Result.failure(java.lang.Exception()))
                }
            }
        } catch (e: java.lang.Exception) {
            userResult.postValue(Result.failure(e))
        }
        return userResult
    }

    override fun resetPassword(email: String): Void {
        TODO("Not yet implemented")
    }
}