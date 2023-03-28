package br.com.bmsrangel.dev.todolist.app.modules.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseBooleanArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TasksFragment : Fragment() {
    private lateinit var gson: Gson
    private lateinit var prefs: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        val activity = requireActivity()
        gson = Gson()
        prefs = activity.getSharedPreferences("todos_kotlin", Context.MODE_PRIVATE)

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, googleSignOptions)

        val todos = getStoredItems()

        val todosListRef = view.findViewById<ListView>(R.id.todosList)
        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_multiple_choice, todos)
        todosListRef.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        todosListRef.adapter = adapter

        val editTextDescription = view.findViewById<EditText>(R.id.editTxtDescription)

        val addButtonRef = view.findViewById<Button>(R.id.btnAdd)
        val removeButtonRef = view.findViewById<Button>(R.id.btnRemove)
        val clearButtonRef = view.findViewById<Button>(R.id.btnClear)
        val logoutButtonRef = view.findViewById<ImageView>(R.id.btnLogout)

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
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        return view
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