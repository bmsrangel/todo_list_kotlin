package br.com.bmsrangel.dev.todolist.app.modules.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.fragments.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.core.fragments.EmailEditTextFragment
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        val toolBarRef = findViewById<MaterialToolbar>(R.id.forgotPasswordToolBar)
        toolBarRef.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val emailEditTextRef = EmailEditTextFragment()
        supportFragmentManager.beginTransaction().replace(R.id.forgotPasswordEditTextFragment, emailEditTextRef).commit()

        val sendForgotPasswordBtnRef = CustomButtonFragment()
        sendForgotPasswordBtnRef.buttonText = getString(R.string.sendEmailBtnText)
        sendForgotPasswordBtnRef.onClick = {
            if (emailEditTextRef.validate()) {
                val email = emailEditTextRef.getText()
                authViewModel.sendForgotPasswordEmail(email)
                onBackPressedDispatcher.onBackPressed()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.sendForgotPasswordEmailBtnFragment, sendForgotPasswordBtnRef).commit()

    }
}