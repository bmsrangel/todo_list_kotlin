package br.com.bmsrangel.dev.todolist.app.core.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.bmsrangel.dev.todolist.R

class WeatherFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("WEATHER", "Fragment created")
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("WEATHER", "Fragment detached")
    }
}