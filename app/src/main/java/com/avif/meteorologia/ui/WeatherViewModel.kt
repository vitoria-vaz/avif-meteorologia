package com.avif.meteorologia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.data.repository.WeatherRepository
import com.avif.meteorologia.ui.screen.components.CitySearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // Default coordinates for São Paulo, Brazil (used as fallback)
    private val defaultLat = -23.5489f
    private val defaultLng = -46.6388f
    
    // Track if we're using GPS location or default
    private val _isUsingGPS = MutableStateFlow(false)
    val isUsingGPS: StateFlow<Boolean> = _isUsingGPS.asStateFlow()
    
    private val _currentCity = MutableStateFlow("Loading location...")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()
    
    private val _isUpdatingLocation = MutableStateFlow(false)
    val isUpdatingLocation: StateFlow<Boolean> = _isUpdatingLocation.asStateFlow()
    
    // API key for OpenWeatherMap Geocoding API
    private val apiKey = "2d1f3afcd57e858c93ded3adebcbebfa" // Use the same API key

    init {
        // We'll wait for GPS coordinates from MainActivity
        // If none arrive within a timeout, we'll use default
        viewModelScope.launch {
            // Wait 3 seconds for GPS, then use default if no GPS data received
            kotlinx.coroutines.delay(3000)
            if (_weatherState.value is WeatherState.Loading) {
                _isUsingGPS.value = false
                fetchWeatherData(defaultLat, defaultLng)
            }
        }
    }

    // Called from MainActivity when GPS coordinates are available
    fun fetchWeatherFromGPS(lat: Float, lng: Float) {
        _isUsingGPS.value = true
        fetchWeatherData(lat, lng)
    }

    fun fetchWeatherData(lat: Float = defaultLat, lng: Float = defaultLng) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                // Mark that we're updating location
                _isUpdatingLocation.value = true
                
                // Update city name based on coordinates if it's not a manual search
                updateLocationName(lat, lng)
                
                val weatherInfo = weatherRepository.getWeatherData(lat, lng)
                _weatherState.value = WeatherState.Success(weatherInfo)
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
            } finally {
                // Done updating location
                _isUpdatingLocation.value = false
            }
        }
    }
    
    // Get city name from coordinates using reverse geocoding
    private suspend fun updateLocationName(lat: Float, lng: Float) {
        try {
            val url = URL("https://api.openweathermap.org/geo/1.0/reverse?lat=$lat&lon=$lng&limit=1&appid=$apiKey")
            
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(response)
            
            if (jsonArray.length() > 0) {
                val cityJson = jsonArray.getJSONObject(0)
                val cityName = cityJson.getString("name")
                _currentCity.value = cityName
            }
        } catch (e: Exception) {
            // If reverse geocoding fails, set to a default city name
            if (_currentCity.value == "Loading location...") {
                _currentCity.value = "São Paulo"
            }
        }
    }
    
    // Update current city and fetch weather
    fun updateCity(city: CitySearchResult) {
        _isUsingGPS.value = false
        _currentCity.value = city.name
        fetchWeatherData(city.lat, city.lon)
    }
    
    // Search for cities using OpenWeatherMap Geocoding API
    suspend fun searchCities(query: String): List<CitySearchResult> {
        return try {
            val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            val url = URL("https://api.openweathermap.org/geo/1.0/direct?q=$encodedQuery&limit=5&appid=$apiKey")
            
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(response)
            
            val cities = mutableListOf<CitySearchResult>()
            for (i in 0 until jsonArray.length()) {
                val cityJson = jsonArray.getJSONObject(i)
                cities.add(
                    CitySearchResult(
                        id = i.toLong(),
                        name = cityJson.getString("name"),
                        country = cityJson.getString("country"),
                        lat = cityJson.getDouble("lat").toFloat(),
                        lon = cityJson.getDouble("lon").toFloat()
                    )
                )
            }
            
            cities
        } catch (e: Exception) {
            emptyList()
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weatherInfo: WeatherInfo) : WeatherState()
    data class Error(val message: String) : WeatherState()
} 