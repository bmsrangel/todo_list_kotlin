package br.com.bmsrangel.dev.todolist.app.modules.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import br.com.bmsrangel.dev.todolist.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_task)

        val editTxtDescription = findViewById<EditText>(R.id.editTxtDescription)
    }
}