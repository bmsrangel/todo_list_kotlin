package br.com.bmsrangel.dev.todolist.app.modules.main.repositories.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.bmsrangel.dev.todolist.app.modules.main.dtos.NewTaskDto
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import javax.inject.Inject

class FirebaseTasksRepositoryImpl @Inject constructor(private val db: FirebaseDatabase): TasksRepository {
    override fun getTasksByUserId(userId: String): Result<LiveData<Array<TaskModel>>> {
        return try {
            val tasksLiveData = MutableLiveData<Array<TaskModel>>()
            val dbRef = db.reference.child("users/$userId/tasks")
            dbRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = snapshot.children.map { dataSnapshot ->
                        val id = dataSnapshot.key
                        val data = dataSnapshot.value as HashMap<*, *>
                        TaskModel(id!!, data["description"].toString(), data["due_date"].toString())
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

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            Result.success(tasksLiveData)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }

    }

    override fun removeTaskById(userId: String, taskId: String) {
        val dbRef = db.reference.child("users/$userId/tasks/$taskId")
        dbRef.removeValue()
    }

    override fun createNewTask(userId: String, newTask: NewTaskDto) {
        val dbRef = db.reference.child("users/$userId/tasks")
        val newTaskRef = dbRef.push()
        newTaskRef.setValue(newTask.toMap())
    }

    override fun updateTask(userId: String, task: TaskModel) {
        val dbRef = db.reference.child("users/$userId/tasks/${task.id}")
        dbRef.setValue(hashMapOf<String, Any>(
            Pair("description", task.description),
            Pair("due_date", task.dueDate)
        ))
    }
}