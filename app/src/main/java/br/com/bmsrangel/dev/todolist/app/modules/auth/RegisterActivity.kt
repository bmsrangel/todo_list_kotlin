package br.com.bmsrangel.dev.todolist.app.modules.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        val editTxtName = findViewById<EditText>(R.id.editTextName)
        val editTxtEmail = findViewById<EditText>(R.id.editTextEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTxtConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener {
            val name = editTxtName.text.toString()
            val email = editTxtEmail.text.toString()
            val password = editTxtPassword.text.toString()
            val confirmPassword = editTxtConfirmPassword.text.toString()
            if (password == confirmPassword) {
                val registerDTO = RegisterDTO(email, password, name)
                authViewModel.register(registerDTO)
                authViewModel.getUser().observe(this, Observer {
                    it.fold({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }, {
                        Toast.makeText(this, R.string.loginFailedError, Toast.LENGTH_SHORT).show()
                    })
                })
            } else {
                Toast.makeText(baseContext, R.string.passwordMismatchError, Toast.LENGTH_SHORT).show()
            }
        }
    }
}