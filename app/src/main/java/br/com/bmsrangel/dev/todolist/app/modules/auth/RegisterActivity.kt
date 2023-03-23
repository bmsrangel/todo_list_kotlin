package br.com.bmsrangel.dev.todolist.app.modules.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import br.com.bmsrangel.dev.todolist.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        val editTxtEmail = findViewById<EditText>(R.id.editTextEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTxtConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener {
            val email = editTxtEmail.text.toString()
            val password = editTxtPassword.text.toString()
            val confirmPassword = editTxtConfirmPassword.text.toString()
            if (password == confirmPassword) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, R.string.userRegistrationFailedError, Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(baseContext, R.string.passwordMismatchError, Toast.LENGTH_SHORT).show()
            }
        }
    }
}