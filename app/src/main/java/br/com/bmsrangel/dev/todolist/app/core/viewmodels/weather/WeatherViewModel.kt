package br.com.bmsrangel.dev.todolist.app.core.viewmodels.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.bmsrangel.dev.todolist.app.core.viewmodels.weather.states.*
import br.com.bmsrangel.dev.todolist.app.modules.main.models.WeatherModel
import br.com.bmsrangel.dev.todolist.app.modules.main.repositories.weather.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository): ViewModel() {
    private val weatherLiveData = MutableLiveData<WeatherState>()
    private var isFirstRun = true

    fun weatherData() = weatherLiveData as LiveData<WeatherState>

    fun getWeatherData(lat: Double, long: Double) {
        if (isFirstRun) {
            weatherLiveData.postValue(LoadingWeatherState())
            isFirstRun = false
        }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                weatherRepository.getCurrentWeather(lat, long)
            }
            result.fold({
                weatherLiveData.postValue(SuccessWeatherState(it))
            }, {
                weatherLiveData.postValue(ErrorWeatherState(it.message))
                weatherLiveData.postValue(InitialWeatherState())
            })
        }
    }
}