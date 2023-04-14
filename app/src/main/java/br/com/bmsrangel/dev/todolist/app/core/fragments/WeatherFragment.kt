package br.com.bmsrangel.dev.todolist.app.core.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.weather.WeatherViewModel
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.weather.states.ErrorWeatherState
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.weather.states.SuccessWeatherState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment: Fragment() {
    private val weatherViewMoModel: WeatherViewModel by viewModels()

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getDeviceLocation()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        val iconWeatherRef = view.findViewById<ImageView>(R.id.iconWeatherFragment)
        val temperatureWeatherRef = view.findViewById<TextView>(R.id.temperatureWeatherFragment)
        val weatherLoadingRef = view.findViewById<ProgressBar>(R.id.loadingWeatherFragment)

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getDeviceLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        weatherViewMoModel.weatherData().observe(requireActivity()) {
            when(it) {
                is SuccessWeatherState -> {
                    val icon = resources.getIdentifier(it.weatherModel.icon, "drawable", requireContext().packageName)
                    iconWeatherRef.setImageResource(icon)

                    val color = Color.parseColor(it.weatherModel.iconColor)
                    iconWeatherRef.setColorFilter(color)

                    temperatureWeatherRef.text = getString(R.string.weatherTemperature, it.weatherModel.temperature.toString())
                    weatherLoadingRef.visibility = View.GONE
                    iconWeatherRef.visibility = View.VISIBLE
                    temperatureWeatherRef.visibility = View.VISIBLE
                }
                is ErrorWeatherState -> {
                    Toast.makeText(requireContext(), getString(R.string.weatherErrorText), Toast.LENGTH_SHORT).show()
                    weatherLoadingRef.visibility = View.GONE
                    iconWeatherRef.visibility = View.GONE
                    temperatureWeatherRef.visibility = View.GONE
                }
                else -> {
                    weatherLoadingRef.visibility = View.VISIBLE
                    iconWeatherRef.visibility = View.GONE
                    temperatureWeatherRef.visibility = View.GONE
                }
            }
        }
        return view
    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val requestIntervalInSeconds = 3600 // 1 hour
        val requestIntervalInMs = (requestIntervalInSeconds * 1000).toLong()
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, requestIntervalInMs, 0f
        ) { location ->
            lifecycleScope.launch {
                weatherViewMoModel.getWeatherData(location.latitude, location.longitude)
            }
        }
    }
}