package br.com.bmsrangel.dev.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firebaseAuth = FirebaseAuth.getInstance()

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
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
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