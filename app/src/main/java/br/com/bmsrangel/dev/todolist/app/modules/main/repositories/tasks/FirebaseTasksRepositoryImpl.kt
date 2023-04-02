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

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            Result.success(tasksLiveData)
        } catch (e: java.lang.Exception) {
            Result.failure(e)
        }

    }

    override fun removeSelectedTasks(userId: String, taskIdList: Array<String>) {
        val dbRef = db.reference.child("users/$userId/tasks")
        for (taskId in taskIdList) {
            dbRef.child(taskId).removeValue()
        }
    }

    override fun createNewTask(userId: String, newTask: NewTaskDto) {
        val dbRef = db.reference.child("users/$userId/tasks")
        val newTaskRef = dbRef.push()
        newTaskRef.setValue(hashMapOf(
            Pair("description", newTask.description)
        ))
    }
}