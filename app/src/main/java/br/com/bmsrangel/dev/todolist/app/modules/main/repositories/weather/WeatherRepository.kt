package br.com.bmsrangel.dev.todolist.app.modules.main.repositories.weather

import br.com.bmsrangel.dev.todolist.app.modules.main.models.WeatherModel

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, long: Double): Result<WeatherModel>
    fun getWeatherIcon(conditionCode: Int): String
    fun getWeatherColor(conditionCode: Int): String
}