package com.avif.meteorologia.data.repository

import android.icu.util.LocaleData
import android.util.Log
import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.data.remote.RemoteDataSource
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import org.json.JSONArray

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {
    companion object {
        private const val TAG = "WeatherRepository"
    }

    override suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo {
        Log.d(TAG, "Getting weather data for lat=$lat, lng=$lng")
        val response = remoteDataSource.getWeatherDataResponse(lat, lng)
        
        // Check if response is valid
        if (response.cod != 200 || response.weather.isEmpty() || response.main == null) {
            val errorMessage = response.message ?: "Unknown error"
            Log.e(TAG, "API Error: $errorMessage (Code: ${response.cod})")
            throw Exception("API Error: $errorMessage (Code: ${response.cod})")
        }
        
        val weather = response.weather[0]
        
        // Make sure we have a valid city name from the API response
        val locationName = if (response.name.isNotEmpty()) {
            Log.d(TAG, "Using city name from API: ${response.name}")
            response.name
        } else {
            Log.w(TAG, "City name is empty in weather response, using fallback method")
            // If API doesn't return a city name, we need to get it another way
            // This could happen with certain coordinates
            getReverseGeocodedCityName(lat, lng) ?: "Unknown location"
        }

        val weatherInfo = WeatherInfo(
            locationName = locationName,
            conditionIcon = weather.icon,
            condition = weather.main,
            temperature = response.main.temp.roundToInt(),
            dayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            isDay = weather.icon.last() == 'd'
        )
        
        Log.d(TAG, "Created WeatherInfo: $weatherInfo")
        return weatherInfo
    }
    
    // Helper method to get city name from coordinates when the weather API doesn't provide it
    private suspend fun getReverseGeocodedCityName(lat: Float, lng: Float): String? {
        return try {
            // Use the same API key as in the RemoteDataSource
            val apiKey = "2d1f3afcd57e858c93ded3adebcbebfa"
            val url = URL("https://api.openweathermap.org/geo/1.0/reverse?lat=$lat&lon=$lng&limit=1&appid=$apiKey")
            
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "Geocoding response in repo: $response")
            val jsonArray = JSONArray(response)
            
            if (jsonArray.length() > 0) {
                val cityJson = jsonArray.getJSONObject(0)
                val name = cityJson.getString("name")
                Log.d(TAG, "Found city name in repo: $name")
                name
            } else {
                Log.d(TAG, "No city found in geocoding response in repo")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in repository geocoding", e)
            null
        }
    }
}