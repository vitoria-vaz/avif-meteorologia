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

        // Calculate UV Index based on cloudiness and conditions
        // This is an estimation since the free API doesn't provide UV Index
        val calculatedUvIndex = calculateApproximateUvIndex(
            clouds = response.clouds?.all ?: 0,
            weatherId = weather.id, 
            isDay = weather.icon.last() == 'd'
        )
        
        // Calculate rain probability based on humidity, clouds and weather condition
        // This is an estimation since the free API doesn't provide precipitation probability
        val calculatedRainProbability = calculateApproximateRainProbability(
            humidity = response.main.humidity,
            clouds = response.clouds?.all ?: 0,
            weatherId = weather.id
        )
        
        val weatherInfo = WeatherInfo(
            locationName = locationName,
            conditionIcon = weather.icon,
            condition = weather.main,
            temperature = response.main.temp.roundToInt(),
            dayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            isDay = weather.icon.last() == 'd',
            // Additional data
            humidity = response.main.humidity,
            windSpeed = response.wind?.speed ?: 0.0,
            pressure = response.main.pressure,
            visibility = response.visibility ?: 10000,
            feelsLike = response.main.feelsLike,
            clouds = response.clouds?.all ?: 0,
            rainProbability = calculatedRainProbability,
            uvIndex = calculatedUvIndex
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
    
    // Estimate UV Index based on cloudiness and weather condition
    // Real UV data requires a different API endpoint or subscription
    private fun calculateApproximateUvIndex(clouds: Int, weatherId: Int, isDay: Boolean): Int {
        if (!isDay) return 0 // No UV at night
        
        // Base value between 0-11 based on cloudiness
        val baseValue = (11 * (100 - clouds) / 100.0).toInt()
        
        // Adjust based on weather condition
        return when {
            // Clear sky conditions
            weatherId in 800..801 -> baseValue.coerceAtMost(11)
            // Partly cloudy
            weatherId in 802..803 -> (baseValue * 0.7).toInt().coerceAtMost(8)
            // Cloudy conditions
            weatherId == 804 -> (baseValue * 0.5).toInt().coerceAtMost(6)
            // Atmosphere conditions (fog, mist, etc.)
            weatherId in 700..799 -> (baseValue * 0.4).toInt().coerceAtMost(4)
            // Rain or drizzle
            weatherId in 300..599 -> (baseValue * 0.3).toInt().coerceAtMost(3)
            // Snow
            weatherId in 600..699 -> (baseValue * 0.6).toInt().coerceAtMost(5)
            // Thunderstorm
            weatherId in 200..299 -> (baseValue * 0.2).toInt().coerceAtMost(2)
            // Default
            else -> baseValue.coerceAtMost(5)
        }
    }
    
    // Estimate rain probability based on humidity, clouds and weather condition
    private fun calculateApproximateRainProbability(humidity: Int, clouds: Int, weatherId: Int): Int {
        // Base probability from humidity and cloudiness
        val baseProbability = (humidity * 0.5 + clouds * 0.5) / 100.0
        
        // Adjust based on weather condition
        val weatherFactor = when {
            // Clear sky
            weatherId == 800 -> 0.1
            // Few clouds
            weatherId == 801 -> 0.2
            // Scattered clouds
            weatherId == 802 -> 0.3
            // Broken clouds
            weatherId == 803 -> 0.4
            // Overcast clouds
            weatherId == 804 -> 0.5
            // Atmosphere conditions (fog, mist, etc.)
            weatherId in 700..799 -> 0.6
            // Drizzle or light rain
            weatherId in 300..399 -> 0.8
            // Rain
            weatherId in 500..599 -> 0.9
            // Thunderstorm
            weatherId in 200..299 -> 0.95
            // Snow
            weatherId in 600..699 -> 0.7
            // Default
            else -> 0.4
        }
        
        return (baseProbability * weatherFactor * 100).toInt().coerceIn(0, 100)
    }
}