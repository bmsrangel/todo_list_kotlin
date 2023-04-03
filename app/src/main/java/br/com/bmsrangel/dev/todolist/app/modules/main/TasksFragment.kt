package br.com.bmsrangel.dev.todolist.app.modules.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.models.UserModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.states.UnauthenticatedAuthState
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import br.com.bmsrangel.dev.todolist.app.modules.main.adapters.TaskAdapter
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import br.com.bmsrangel.dev.todolist.app.modules.main.states.SuccessTasksState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@AndroidEntryPoint
class TasksFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels()
    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var user: UserModel

    override fun onStart() {
        super.onStart()
        authViewModel.getUserFromLocalStorage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        val activity = requireActivity()

        val googleSignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, googleSignOptions)

        val logoutButtonRef = view.findViewById<ImageView>(R.id.btnLogout)

        val newTaskButtonRef = view.findViewById<FloatingActionButton>(R.id.btnNewTask)

        val taskListViewRef = view.findViewById<ListView>(R.id.todosList)
        val progressIndicator = view.findViewById<ProgressBar>(R.id.tasksProgressBar)
        progressIndicator.visibility = View.VISIBLE
        taskListViewRef.visibility = View.GONE

        val tasks = arrayListOf<TaskModel>()
        val adapter = TaskAdapter(activity, tasks)
        taskListViewRef.adapter = adapter

        val editTxtDescription = view.findViewById<EditText>(R.id.editTxtDescription)
        val addTaskBtnRef = view.findViewById<Button>(R.id.btnAdd)
        val removeBtnRef = view.findViewById<Button>(R.id.btnRemove)

        logoutButtonRef.setOnClickListener {
            authViewModel.signOut()
            // TODO: Solve Google signout
            googleSignInClient.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        addTaskBtnRef.setOnClickListener {
            val description = editTxtDescription.text.toString()
            val newTaskDto = NewTaskDto(description)
            tasksViewModel.createTask(user.uid, newTaskDto)
            editTxtDescription.text.clear()
        }

        authViewModel.getUser().observe(activity) {
            if (it is UnauthenticatedAuthState) {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            } else if (it is SuccessAuthState) {
                user = it.user
                tasksViewModel.fetchTasks(user.uid)
                tasksViewModel.tasks().observe(activity) { tasksState ->
                    if (tasksState is SuccessTasksState) {
                        val taskList = tasksState.taskList
                        tasks.clear()
                        tasks.addAll(taskList)
                        adapter.notifyDataSetChanged()
                        progressIndicator.visibility = View.GONE
                        taskListViewRef.visibility = View.VISIBLE

                        taskListViewRef.setOnItemClickListener { parent, view, position, id ->
                            val selectedTask = taskList[position]
                            val intent = Intent(activity, SingleTaskActivity::class.java)
                            val serializedTask = Json.encodeToString(selectedTask)
                            intent.putExtra("task", serializedTask)
                            startActivity(intent)
                        }
                    }
                }

            }
        }
        newTaskButtonRef.setOnClickListener {
            val intent = Intent(activity, SingleTaskActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}