package br.com.bmsrangel.dev.todolist.app.modules.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.repositories.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    @Inject
    lateinit var authRepository: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        val editTxtEmail = findViewById<EditText>(R.id.editTextEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTxtConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener {
            val email = editTxtEmail.text.toString()
            val password = editTxtPassword.text.toString()
            val confirmPassword = editTxtConfirmPassword.text.toString()
            if (password == confirmPassword) {
                val newUserDTO = RegisterDTO(email, password)
                val isRegistrationSuccessful = authRepository.register(newUserDTO)
                if (isRegistrationSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, R.string.userRegistrationFailedError, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, R.string.passwordMismatchError, Toast.LENGTH_SHORT).show()
            }
        }
    }
}