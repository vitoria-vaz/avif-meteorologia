package com.avif.meteorologia.data.repository

import android.icu.util.LocaleData
import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.data.remote.RemoteDataSource
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo {
        val response = remoteDataSource.getWeatherDataResponse(lat, lng)
        
        // Check if response is valid
        if (response.cod != 200 || response.weather.isEmpty() || response.main == null) {
            val errorMessage = response.message ?: "Unknown error"
            throw Exception("API Error: $errorMessage (Code: ${response.cod})")
        }
        
        val weather = response.weather[0]

        return WeatherInfo(
            locationName = response.name,
            conditionIcon = weather.icon,
            condition = weather.main,
            temperature = response.main.temp.roundToInt(),
            dayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            isDay = weather.icon.last() == 'd'
        )
    }
}