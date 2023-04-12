package br.com.bmsrangel.dev.todolist.app.modules.main.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.weather.states.ErrorWeatherState
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.weather.states.SuccessWeatherState
import br.com.bmsrangel.dev.todolist.app.modules.main.viewmodels.weather.states.WeatherState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : Fragment() {
//    private val weatherViewModel: WeatherViewModel by viewModels()

    companion object {
        private val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
//            weatherViewModel.weather().observe(viewLifecycleOwner) {
//                updateUi(it, view)
//            }
            getDeviceLocation(view)
        }
        return view
    }

    private fun getDeviceLocation(view: View) {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object:
            LocationListener{
            override fun onLocationChanged(location: Location) {
//                weatherViewModel.getWeather(location.latitude, location.longitude)
                println("${location.latitude},${location.latitude}")
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })

    }

    fun updateUi(weatherState: WeatherState, view: View) {
        if (weatherState is SuccessWeatherState) {
            val iconImageViewRef = view.findViewById<ImageView>(R.id.weatherIcon)
            val iconTempTextViewRef = view.findViewById<TextView>(R.id.weatherTemp)

            val icon = resources.getIdentifier(weatherState.weatherModel.icon, "drawable", requireContext().packageName)
            iconImageViewRef.setImageResource(icon)
            val colorString = weatherState.weatherModel.iconColor
            val iconColor = Color.parseColor(colorString)
            iconImageViewRef.setColorFilter(iconColor)

            iconTempTextViewRef.text = "${weatherState.weatherModel.temperature}°C"

        } else if (weatherState is ErrorWeatherState) {
            Toast.makeText(requireActivity(), weatherState.message, Toast.LENGTH_SHORT).show()
        }
    }
}