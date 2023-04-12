package br.com.bmsrangel.dev.todolist.app.modules.main.repositories.weather

import br.com.bmsrangel.dev.todolist.app.modules.main.models.WeatherModel
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject

class URLWeatherRepositoryImpl @Inject constructor(): WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, long: Double): Result<WeatherModel> {
        return try {
            val apiKey = "7ad0666b41804739844233114231104"
            val baseUrl = "http://api.weatherapi.com/v1"
            val url = "$baseUrl/current.json?key=$apiKey&q=$lat,$long"
            val result = URL(url).readText()
            val data = JSONObject(result)
            val temperature = data["temp_c"] as Float
            val conditionCode = (data["condition"] as Map<*, *>)["code"] as Int
            val icon = getWeatherIcon(conditionCode)
            val iconColor = getWeatherColor(conditionCode)
            Result.success(WeatherModel(temperature, icon, iconColor))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getWeatherIcon(conditionCode: Int): String {
        return when(conditionCode) {
            in 200 .. 232 -> "wi_thunderstorm"
            in 300 .. 321 -> "wi_showers"
            in 500 .. 531 -> "wi_rain"
            in 600 .. 622 -> "wi_snow"
            in 701 .. 781 -> "wi_fog"
            800 -> "wi_day_sunny"
            801 -> "wi_day_cloud"
            802 -> "wi_cloud"
            803, 804 -> "wi_day_cloud_high"
            1183 -> "wi_day_light_wind"
            else -> "wi_day_sunny"
        }
    }

    override fun getWeatherColor(conditionCode: Int): String {
        return when(conditionCode) {
            in 200..232 -> "#637E90"
            in 300..321 -> "#29B3FF"
            in 500..531 -> "#14C2DD"
            in 600..622 -> "#E5F2F0"
            in 701..781 -> "#FFFEA8"
            800 -> "#FBC740"
            801 -> "#BCECE0"
            802 -> "#BCECE0"
            803, 804 -> "#36EEE0"
            1183 -> "#14C2DD"
            else -> "#FBC740"
        }
    }
}