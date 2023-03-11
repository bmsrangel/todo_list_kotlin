package br.com.bmsrangel.dev.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.core.util.keyIterator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val todos = arrayListOf<String>("Task 1", "Task 2", "Task 3", "Task 4")

        val todosListRef = findViewById<ListView>(R.id.todosList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, todos)
        todosListRef.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        todosListRef.adapter = adapter

        val editTextDescription = findViewById<EditText>(R.id.editTxtDescription)

        val addButtonRef = findViewById<Button>(R.id.btnAdd)
        val removeButtonRef = findViewById<Button>(R.id.btnRemove)
        val clearButtonRef = findViewById<Button>(R.id.btnClear)

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
    }
}