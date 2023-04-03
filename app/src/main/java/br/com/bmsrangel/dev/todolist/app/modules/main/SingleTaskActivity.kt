package br.com.bmsrangel.dev.todolist.app.modules.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@AndroidEntryPoint
class SingleTaskActivity : AppCompatActivity() {
    private var selectedTask: TaskModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_task)

        supportActionBar?.hide()

        val toolBarRef = findViewById<MaterialToolbar>(R.id.newTaskToolBar)
        toolBarRef.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val editTxtDescription = findViewById<EditText>(R.id.editTxtDescription)
        val serializedSelectedTask: String? = intent.getStringExtra("task")
        if (serializedSelectedTask != null) {
            selectedTask = Json.decodeFromString<TaskModel>(serializedSelectedTask)
            editTxtDescription.text = Editable.Factory.getInstance().newEditable(selectedTask!!.description)
        }

    }
}