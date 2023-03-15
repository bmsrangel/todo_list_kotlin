package br.com.bmsrangel.dev.todolist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var gson: Gson
    private lateinit var prefs: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        gson = Gson()
        prefs = getSharedPreferences("todos_kotlin", Context.MODE_PRIVATE)

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignOptions)

        val todos = getStoredItems()

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
            updateStoredItems(todos)
            adapter.notifyDataSetChanged()
            editTextDescription.text.clear()
        }

        removeButtonRef.setOnClickListener {
            val selectedTodos: SparseBooleanArray = todosListRef.checkedItemPositions
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
            updateStoredItems(todos)
        }

        clearButtonRef.setOnClickListener {
            todos.clear()
            adapter.notifyDataSetChanged()
            updateStoredItems(todos)
        }

        logoutButtonRef.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getStoredItems(): ArrayList<String> {
        val itemsJson = prefs.getString("items", null)
        return if (itemsJson.isNullOrEmpty()) {
            arrayListOf()
        } else {
            val items = gson.fromJson<ArrayList<String>>(itemsJson, object: TypeToken<ArrayList<String>>(){}.type)
            items
        }
    }

    private fun updateStoredItems(items: ArrayList<String>) {
        val itemsJson = gson.toJson(items)
        val prefsEditor = prefs.edit()
        prefsEditor.putString("items", itemsJson)
        prefsEditor.apply()
    }
}