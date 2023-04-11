package br.com.bmsrangel.dev.todolist.app.core.repositories.auth

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.ProfileDTO
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    AuthRepository {
    override suspend fun login(loginDTO: LoginDTO): Result<UserModel> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(loginDTO.email, loginDTO.password).await()
            val firebaseUser = result.user!!
            val userModel = UserModel(firebaseUser.uid, firebaseUser.displayName, firebaseUser.email!!, firebaseUser.photoUrl.toString())
            Result.success(userModel)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(registerDTO: RegisterDTO): Result<UserModel> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(registerDTO.email, registerDTO.password).await()
            val firebaseUser = result.user!!
            val profileUpdates = userProfileChangeRequest {
                displayName = registerDTO.name
            }
            firebaseUser.updateProfile(profileUpdates).await()
            val userModel = UserModel(firebaseUser.uid, firebaseUser.displayName, firebaseUser.email!!, firebaseUser.photoUrl.toString())
            Result.success(userModel)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun signInWithGoogle(accountIdToken: String): Result<UserModel> {
        return try {
            val credential = GoogleAuthProvider.getCredential(accountIdToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!
            val userModel = UserModel(firebaseUser.uid, firebaseUser.displayName, firebaseUser.email!!, firebaseUser.photoUrl.toString())
            Result.success(userModel)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun updatePassword(newPassword: String): Result<Boolean> {
        return try {
            firebaseAuth.currentUser!!.updatePassword(newPassword).await()
            return Result.success(true)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(profileDTO: ProfileDTO): Result<Boolean> {
        val profileUpdates = UserProfileChangeRequest.Builder()
        if (profileDTO.name != null) {
            profileUpdates.displayName = profileDTO.name
        }
        if (profileDTO.photoUri != null) {
            profileUpdates.photoUri = profileDTO.photoUri
        }
        return try {
            firebaseAuth.currentUser!!.updateProfile(profileUpdates.build()).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }


    }
}