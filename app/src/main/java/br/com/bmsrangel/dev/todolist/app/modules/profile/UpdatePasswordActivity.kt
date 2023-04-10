package br.com.bmsrangel.dev.todolist.app.modules.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.fragments.CustomButtonFragment
import br.com.bmsrangel.dev.todolist.app.core.fragments.PasswordEditTextFragment
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.ErrorAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.auth.states.PasswordUpdateSuccessAuthState
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        supportActionBar?.hide()

        val toolBarRef = findViewById<MaterialToolbar>(R.id.updatePasswordToolBar)
        toolBarRef.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val updatePasswordTextEditRef = PasswordEditTextFragment()
        supportFragmentManager.beginTransaction().replace(R.id.updatePasswordTextEditFragment, updatePasswordTextEditRef).commit()

        val updatePasswordBtnRef = CustomButtonFragment()
        updatePasswordBtnRef.buttonText = getString(R.string.updatePasswordText)
        updatePasswordBtnRef.onClick = {
            authViewModel.getUser().observe(this) {
                if (it is PasswordUpdateSuccessAuthState) {
                    onBackPressedDispatcher.onBackPressed()
                } else if (it is ErrorAuthState) {
                    Toast.makeText(this, getString(R.string.passwordUpdateErrorText), Toast.LENGTH_SHORT).show()
                }
            }
            val newPassword = updatePasswordTextEditRef.getText()
            authViewModel.updatePassword(newPassword)
        }
        supportFragmentManager.beginTransaction().replace(R.id.updatePasswordActionBtnFragment, updatePasswordBtnRef).commit()
    }
}