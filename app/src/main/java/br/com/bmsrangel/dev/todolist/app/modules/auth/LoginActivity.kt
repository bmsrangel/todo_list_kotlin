package br.com.bmsrangel.dev.todolist.app.modules.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.ErrorAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        val editTxtEmail = findViewById<EditText>(R.id.editTextLoginEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTextLoginPassword)

        val loginButtonRef = findViewById<Button>(R.id.btnLogin)
        val registerButtonRef = findViewById<Button>(R.id.btnRegister)

        loginButtonRef.setOnClickListener {
            val email = editTxtEmail.text.toString()
            val password = editTxtPassword.text.toString()

            val loginDTO = LoginDTO(email, password)
            authViewModel.login(loginDTO)

            authViewModel.getUser().observe(this, Observer {
                if (it is SuccessAuthState) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (it is ErrorAuthState) {
                    Toast.makeText(this, R.string.loginFailedError, Toast.LENGTH_SHORT).show()
                }
            })
        }

        registerButtonRef.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}