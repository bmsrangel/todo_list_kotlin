package br.com.bmsrangel.dev.todolist.app.modules.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.AuthViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.states.SuccessAuthState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.states.UnauthenticatedAuthState
import br.com.bmsrangel.dev.todolist.app.modules.auth.LoginActivity
import br.com.bmsrangel.dev.todolist.app.modules.main.states.SuccessTasksState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels()
    private val tasksViewModel: TasksViewModel by viewModels()

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

        val taskListViewRef = view.findViewById<ListView>(R.id.todosList)
        val progressIndicator = view.findViewById<ProgressBar>(R.id.tasksProgressBar)
        progressIndicator.visibility = View.VISIBLE
        taskListViewRef.visibility = View.GONE
        val tasks = arrayListOf<String>()
        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_multiple_choice, tasks)
        taskListViewRef.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        taskListViewRef.adapter = adapter

        logoutButtonRef.setOnClickListener {
            authViewModel.signOut()
            // TODO: Solve Google signout
            googleSignInClient.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        authViewModel.getUser().observe(activity) {
            if (it is UnauthenticatedAuthState) {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            } else if (it is SuccessAuthState) {
                val user = it.user
                tasksViewModel.fetchTasks(user.uid)
                tasksViewModel.tasks().observe(activity) { tasksState ->
                    if (tasksState is SuccessTasksState) {
                        val taskList = tasksState.taskList
                        val tasksDescriptions = taskList.map { task -> task.description }
                        tasks.clear()
                        tasks.addAll(tasksDescriptions)
                        adapter.notifyDataSetChanged()
                        progressIndicator.visibility = View.GONE
                        taskListViewRef.visibility = View.VISIBLE
                    }
                }

            }
        }
        return view
    }
}