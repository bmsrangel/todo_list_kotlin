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
import br.com.bmsrangel.dev.todolist.app.core.components.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.core.dtos.RegisterDTO
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.ErrorAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.modules.auth.fragments.GoogleLoginFragment
import com.google.android.material.appbar.MaterialToolbar
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
        val toolBarRef = findViewById<MaterialToolbar>(R.id.registerToolBar)
        val googleLoginBtnRef = GoogleLoginFragment()
        googleLoginBtnRef.buttonText = getString(R.string.googleRegisterButtonText)
        supportFragmentManager.beginTransaction().replace(R.id.googleLoginFragment, googleLoginBtnRef).commit()

        toolBarRef.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnCreateAccount = CustomButtonFragment()
        btnCreateAccount.buttonText = getString(R.string.createAccountButtonText)
        btnCreateAccount.onClick = {
            val name = editTxtName.text.toString()
            val email = editTxtEmail.text.toString()
            val password = editTxtPassword.text.toString()
            val confirmPassword = editTxtConfirmPassword.text.toString()
            if (password == confirmPassword) {
                val registerDTO = RegisterDTO(email, password, name)
                authViewModel.register(registerDTO)
                authViewModel.getUser().observe(this, Observer {
                    if (it is SuccessAuthState) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (it is ErrorAuthState) {
                        Toast.makeText(this, R.string.loginFailedError, Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(baseContext, R.string.passwordMismatchError, Toast.LENGTH_SHORT).show()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.createAccountBtnFragment, btnCreateAccount).commit()
    }
}