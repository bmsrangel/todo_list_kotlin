package br.com.bmsrangel.dev.todolist.app.core.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.com.bmsrangel.dev.todolist.R
import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.weather.WeatherRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment: Fragment() {
    @Inject
    lateinit var weatherRepository: WeatherRepository
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("WEATHER", "Fragment created")
        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getDeviceLocation(view)
        }else{
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
        return view
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("WEATHER", "Fragment detached")
    }

    private fun getDeviceLocation(view: View) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f
        ) { location ->
            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    weatherRepository.getCurrentWeather(location.latitude, location.longitude)

                }
                Log.d("LOCATION", "${location.latitude},${location.longitude}")
                result.fold({
                    Log.d("RESULT", "${it.temperature},${it.icon}")
                }, {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}
