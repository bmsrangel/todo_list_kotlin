package br.com.bmsrangel.dev.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onStart() {
        super.onStart()
        var currentUser = auth.currentUser

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        val firebaseAuth = FirebaseAuth.getInstance();

        val editTxtEmail = findViewById<EditText>(R.id.editTextLoginEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTextLoginPassword)

        val loginButtonRef = findViewById<Button>(R.id.btnLogin)
        val googleLoginButtonRef = findViewById<Button>(R.id.btnGoogleLogin)
        val registerButtonRef = findViewById<Button>(R.id.btnRegister)

        loginButtonRef.setOnClickListener {
            val email = editTxtEmail.text.toString()
            val password = editTxtPassword.text.toString()
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        googleLoginButtonRef.setOnClickListener {
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.webClientId))
                        .setFilterByAuthorizedAccounts(true)
                        .build()
                ).build()
        }

        registerButtonRef.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


}