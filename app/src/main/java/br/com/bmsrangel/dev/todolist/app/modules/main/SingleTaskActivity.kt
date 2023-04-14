package br.com.bmsrangel.dev.todolist.app.modules.main

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import br.com.bmsrangel.dev.todolist.BuildConfig
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.services.notifications.NotificationService
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.single_task.SingleTaskViewModel
import br.com.bmsrangel.dev.todolist.databinding.ActivitySingleTaskBinding
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.Calendar

@AndroidEntryPoint
class SingleTaskActivity : AppCompatActivity() {
    private val singleTaskViewModel: SingleTaskViewModel by viewModels()

    private var selectedTask: TaskModel? = null
    private lateinit var userId: String

    private lateinit var binding: ActivitySingleTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                scheduleNotification(newTaskDto.dueDate, getString(R.string.taskCreatedText), getString(R.string.taskCreatedMessageText, newTaskDto.description , newTaskDto.dueDate))
            } else {
                val updatedTask = selectedTask!!.copy(description = editTxtDescriptionRef.text.toString(), dueDate = editTxtDateRef.text.toString())
                singleTaskViewModel.updateTask(updatedTask, userId)
                scheduleNotification(updatedTask.dueDate, getString(R.string.taskUpdatedText), getString(R.string.taskUpdatedMessageText, updatedTask.description, updatedTask.dueDate))
            }
            onBackPressedDispatcher.onBackPressed()
        }

            createNotificationChannel()
    }

    private fun getFormattedDate(year: Int, month: Int, dayOfMonth: Int): String {
        val currentMonth = month + 1
        return String.format("%02d/%02d/%04d", dayOfMonth, currentMonth, year)
    }

    private fun createNotificationChannel() {
        val name = "TodoList"
        val descriptionText = "Channel for Todo List notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("TODO", name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification(scheduleDate: String, title: String, message: String) {
        val intent = Intent(this, NotificationService::class.java)

        intent.putExtra("title", title)
        intent.putExtra("message", message)

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dateRegex = "(\\d{2})/(\\d{2})/(\\d{4})".toRegex()
        val matchResult = dateRegex.find(scheduleDate)
        val calendar = Calendar.getInstance()
        val (day, month, year) = matchResult!!.destructured

        // Flag used for testing notification in development environment
        if (BuildConfig.DEBUG) {
            calendar.add(Calendar.MINUTE, 1)
        } else {
            calendar.set(year.toInt(), month.toInt(), day.toInt(), 0, 0, 0)
        }

        Log.d("SCHEDULE", "Scheduled to ${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)}/${calendar.get(Calendar.YEAR)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
        Toast.makeText(this, getString(R.string.notificationScheduledText), Toast.LENGTH_SHORT).show()
    }
}