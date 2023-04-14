package br.com.bmsrangel.dev.todolist.app.modules.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.fragments.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.core.fragments.EmailEditTextFragment
import br.com.bmsrangel.dev.todolist.app.core.dtos.LoginDTO
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.ErrorAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.modules.auth.fragments.GoogleLoginFragment
import br.com.bmsrangel.dev.todolist.app.modules.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        val editTxtEmail = EmailEditTextFragment()
        supportFragmentManager.beginTransaction().replace(R.id.editTextEmailFragment, editTxtEmail).commit()
        val editTxtPassword = findViewById<EditText>(R.id.editTextLoginPassword)
        val forgotPasswordBtnRef = findViewById<Button>(R.id.forgotPasswordBtn)
        forgotPasswordBtnRef.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val loginButtonRef = CustomButtonFragment()
        loginButtonRef.buttonText = getString(R.string.loginButtonText)
        loginButtonRef.onClick = {
            val email = editTxtEmail.getText()
            val password = editTxtPassword.text.toString()

            if (editTxtEmail.validate()) {
                val loginDTO = LoginDTO(email, password)
                authViewModel.login(loginDTO)

                authViewModel.getUser().observe(this) {
                    if (it is SuccessAuthState) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (it is ErrorAuthState) {
                        Toast.makeText(this, R.string.loginFailedError, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.loginBtnFragment, loginButtonRef).commit()

        val registerButtonRef = findViewById<Button>(R.id.btnRegister)
        val googleLoginButtonRef = GoogleLoginFragment()
        googleLoginButtonRef.buttonText = getString(R.string.googleLoginButtonText)
        supportFragmentManager.beginTransaction().replace(R.id.googleLoginFragment, googleLoginButtonRef).commit()

        registerButtonRef.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}