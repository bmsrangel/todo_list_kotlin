package br.com.bmsrangel.dev.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.core.util.keyIterator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val firebaseAuth = FirebaseAuth.getInstance()

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignOptions)

        val todos = arrayListOf<String>("Task 1", "Task 2", "Task 3", "Task 4")

        val todosListRef = findViewById<ListView>(R.id.todosList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, todos)
        todosListRef.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        todosListRef.adapter = adapter

        val editTextDescription = findViewById<EditText>(R.id.editTxtDescription)

        val addButtonRef = findViewById<Button>(R.id.btnAdd)
        val removeButtonRef = findViewById<Button>(R.id.btnRemove)
        val clearButtonRef = findViewById<Button>(R.id.btnClear)
        val logoutButtonRef = findViewById<ImageView>(R.id.btnLogout)

        addButtonRef.setOnClickListener {
            val description = editTextDescription.text.toString()
            todos.add(description)
            adapter.notifyDataSetChanged()
            editTextDescription.text.clear()
        }

        removeButtonRef.setOnClickListener {
            val selectedTodos: SparseBooleanArray = todosListRef.checkedItemPositions;
            val todosLength = todosListRef.count
            var cursor = todosLength - 1

            while (cursor >= 0) {
                if (selectedTodos.get(cursor)) {
                    adapter.remove(todos[cursor])
                }
                cursor--
            }
            selectedTodos.clear()
            adapter.notifyDataSetChanged()
        }

        clearButtonRef.setOnClickListener {
            todos.clear()
            adapter.notifyDataSetChanged()
        }

        logoutButtonRef.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}