package br.com.bmsrangel.dev.todolist.app.core.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import br.com.bmsrangel.dev.todolist.R

class ListViewFragment : Fragment() {
    private lateinit var list: ListView
    var adapter: BaseAdapter? = null
    var setOnItemClickListener: ((position: Int) -> Unit)? = null
    var setOnItemLongClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_list_view, container, false)
        this.list = view.findViewById<ListView>(R.id.todosList)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        list.setOnItemClickListener { _, _, i, _ -> setOnItemClickListener?.invoke(i) }
        list.setOnItemLongClickListener { _, _, i, _ ->
            setOnItemLongClickListener?.invoke(i)
            true
        }
    }

    fun setListVisibility(visibility: Int) {
        if (::list.isInitialized) {
            list.visibility = visibility
        }
    }
}