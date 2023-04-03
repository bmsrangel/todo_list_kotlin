package br.com.bmsrangel.dev.todolist.app.modules.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.bmsrangel.dev.todolist.app.modules.main.models.TaskModel

class TaskAdapter(context: Context, private val taskList: List<TaskModel>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return taskList.size
    }

    override fun getItem(p0: Int): Any {
        return taskList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1

        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_1, p2, false)
        }

        val task = getItem(p0) as TaskModel
        val textView = view!!.findViewById<TextView>(android.R.id.text1)
        textView.text = task.description

        return view
    }
}