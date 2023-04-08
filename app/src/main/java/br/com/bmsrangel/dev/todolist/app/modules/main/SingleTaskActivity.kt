package br.com.bmsrangel.dev.todolist.app.modules.main

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.single_task.SingleTaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.Calendar

@AndroidEntryPoint
class SingleTaskActivity : AppCompatActivity() {
    private var selectedTask: TaskModel? = null
    private val singleTaskViewModel: SingleTaskViewModel by viewModels()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_task)

        supportActionBar?.hide()

        val editTxtDescriptionRef = findViewById<EditText>(R.id.editTxtDescription)
        val editTxtDateRef = findViewById<EditText>(R.id.editTxtDate)
        val btnSelectDateRef = findViewById<Button>(R.id.btnSelectDate)
        val btnSaveTaskRef = findViewById<Button>(R.id.btnSaveTask)
        val toolBarRef = findViewById<MaterialToolbar>(R.id.newTaskToolBar)

        val serializedSelectedTask: String? = intent.getStringExtra("task")
        userId = intent.getStringExtra("userId")!!

        val currentDateTime = Calendar.getInstance()
        var currentYear = currentDateTime.get(Calendar.YEAR)
        var currentMonth = currentDateTime.get(Calendar.MONTH)
        var currentDayOfMonth = currentDateTime.get(Calendar.DAY_OF_MONTH)

        toolBarRef.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val formattedCurrentDate = getFormattedDate(currentYear, currentMonth, currentDayOfMonth)
        editTxtDateRef.setText(formattedCurrentDate)

        if (serializedSelectedTask != null) {
            selectedTask = Json.decodeFromString<TaskModel>(serializedSelectedTask)
            toolBarRef.title = getString(R.string.updateTaskText)
            editTxtDescriptionRef.setText(selectedTask!!.description)
            editTxtDateRef.setText(selectedTask!!.dueDate)
        }

        btnSelectDateRef.setOnClickListener {
            val datePicker = DatePickerDialog(this, {_, year, month, dayOfMonth ->
                editTxtDateRef.setText(getFormattedDate(year, month, dayOfMonth))
                currentYear = year
                currentMonth = month
                currentDayOfMonth = dayOfMonth
            }, currentYear, currentMonth, currentDayOfMonth)
            datePicker.show()
        }

        btnSaveTaskRef.setOnClickListener {
            if (selectedTask == null) {
                val newTaskDto = NewTaskDto(editTxtDescriptionRef.text.toString(), editTxtDateRef.text.toString())
                singleTaskViewModel.createTask(newTaskDto, userId)
            } else {
                val updatedTask = selectedTask!!.copy(description = editTxtDescriptionRef.text.toString(), dueDate = editTxtDateRef.text.toString())
                singleTaskViewModel.updateTask(updatedTask, userId)
            }
            onBackPressedDispatcher.onBackPressed()

        }

    }

    private fun getFormattedDate(year: Int, month: Int, dayOfMonth: Int): String {
        val currentMonth = month + 1
        return String.format("%02d/%02d/%04d", dayOfMonth, currentMonth, year)
    }
}