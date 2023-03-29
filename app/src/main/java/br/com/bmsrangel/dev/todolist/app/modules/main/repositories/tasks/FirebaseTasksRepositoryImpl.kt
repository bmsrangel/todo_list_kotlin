package br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class FirebaseTasksRepositoryImpl @Inject constructor(private val db: FirebaseDatabase): TasksRepository {
    override fun getTasksByUserId(userId: String): Result<LiveData<Array<TaskModel>>> {
        return try {
            val tasksLiveData = MutableLiveData<Array<TaskModel>>()
            val dbRef = db.reference.child("users/$userId/tasks")
            dbRef.get().addOnSuccessListener {
                val tasks = it.children.map { dataSnapshot ->
                    val id = dataSnapshot.key
                    val data = dataSnapshot.value as HashMap<*, *>
                    TaskModel(id!!, data["description"].toString())
                }
                tasksLiveData.value = tasks.toTypedArray()
                /* Snapshot format:
                    {
                        "key": 1,
                        "value": {
                            "description": "Task 1"
                        }
                    }
                 */
            }
            Result.success(tasksLiveData)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }

    }
}