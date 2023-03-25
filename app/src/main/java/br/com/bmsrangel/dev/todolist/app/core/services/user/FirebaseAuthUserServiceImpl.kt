package br.com.bmsrangel.dev.todolist.app.core.services.user

import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthUserServiceImpl @Inject constructor(private val firebaseAuth: FirebaseAuth): UserService {
    override fun getUser(): UserModel? {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            UserModel(user.uid, user.displayName, user.email!!, user.photoUrl.toString())
        } else {
            null
        }
    }
}