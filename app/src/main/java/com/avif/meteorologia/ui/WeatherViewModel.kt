package com.avif.meteorologia.ui

import android.util.Log
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
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    companion object {
        private const val TAG = "WeatherViewModel"
    }
    
    // Track if we're using GPS location or default
    private val _isUsingGPS = MutableStateFlow(false)
    val isUsingGPS: StateFlow<Boolean> = _isUsingGPS.asStateFlow()
    
    private val _currentCity = MutableStateFlow("Waiting for location...")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()
    
    private val _isUpdatingLocation = MutableStateFlow(false)
    val isUpdatingLocation: StateFlow<Boolean> = _isUpdatingLocation.asStateFlow()
    
    // Add a new state for tracking refresh operation
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    // Add current coordinates for use in refresh
    private var currentLatitude: Float = 0f
    private var currentLongitude: Float = 0f
    
    // API key for OpenWeatherMap Geocoding API
    private val apiKey = "2d1f3afcd57e858c93ded3adebcbebfa" // Use the same API key

    init {
        // We now rely only on GPS coordinates from MainActivity
        // The UI will stay in loading state until location is received
        Log.d(TAG, "ViewModel initialized - waiting for GPS data")
    }

    // Called from MainActivity when GPS coordinates are available
    fun fetchWeatherFromGPS(lat: Float, lng: Float) {
        Log.d(TAG, "Fetching weather from GPS: lat=$lat, lng=$lng")
        _isUsingGPS.value = true
        // Store current coordinates for refresh operations
        currentLatitude = lat
        currentLongitude = lng
        fetchWeatherData(lat, lng)
    }

    private fun fetchWeatherData(lat: Float, lng: Float) {
        viewModelScope.launch {
            Log.d(TAG, "fetchWeatherData called with lat=$lat, lng=$lng")
            _weatherState.value = WeatherState.Loading
            
            // Store current coordinates for refresh operations
            currentLatitude = lat
            currentLongitude = lng
            
            try {
                // Mark that we're updating location
                _isUpdatingLocation.value = true
                
                // Pre-fetch the location name to show something immediately
                updateLocationName(lat, lng)
                
                Log.d(TAG, "Calling weather repository for lat=$lat, lng=$lng")
                val weatherInfo = weatherRepository.getWeatherData(lat, lng)
                
                // Update the city name with the one from the weather API response
                if (weatherInfo.locationName != "Unknown location" && weatherInfo.locationName.isNotEmpty()) {
                    Log.d(TAG, "Updating city name from weather API: ${weatherInfo.locationName}")
                    _currentCity.value = weatherInfo.locationName
                } else {
                    Log.d(TAG, "Weather API didn't return a valid city name, keeping current: ${_currentCity.value}")
                }
                
                Log.d(TAG, "Weather data received: ${weatherInfo.locationName}, temp: ${weatherInfo.temperature}")
                _weatherState.value = WeatherState.Success(weatherInfo)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching weather data", e)
                _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
            } finally {
                // Done updating location
                _isUpdatingLocation.value = false
            }
        }
    }
    
    // Get city name from coordinates using reverse geocoding
    private fun updateLocationName(lat: Float, lng: Float) {
        // Skip the additional API call for city name if this is from a manual search
        // The weather API will already provide the city name and we'll use that instead
        // This is just for getting an initial city name before the weather API responds
        if (!_isUsingGPS.value && _currentCity.value != "Waiting for location...") {
            Log.d(TAG, "Skipping location name update for manual city selection")
            return
        }
        
        try {
            Log.d(TAG, "Updating location name for lat=$lat, lng=$lng")
            val url = URL("https://api.openweathermap.org/geo/1.0/reverse?lat=$lat&lon=$lng&limit=1&appid=$apiKey")
            
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000 // 10 seconds timeout
            connection.readTimeout = 10000
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "Geocoding response: $response")
            val jsonArray = JSONArray(response)
            
            if (jsonArray.length() > 0) {
                val cityJson = jsonArray.getJSONObject(0)
                val cityName = cityJson.getString("name")
                Log.d(TAG, "Found city name: $cityName")
                _currentCity.value = cityName
            } else {
                Log.d(TAG, "No city found in geocoding response")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating location name", e)
        }
    }
    
    // Update current city and fetch weather
    fun updateCity(city: CitySearchResult) {
        Log.d(TAG, "Manually updating city: ${city.name}, lat=${city.lat}, lon=${city.lon}")
        _isUsingGPS.value = false
        _currentCity.value = city.name
        // Store coordinates for refresh
        currentLatitude = city.lat
        currentLongitude = city.lon
        fetchWeatherData(city.lat, city.lon)
    }
    
    /**
     * Refresh weather data using the last known coordinates
     * This method can be called from multiple places:
     * - Pull-to-refresh gesture
     * - Periodic refresh timer
     * - Manual refresh button
     * - After returning to the app from background
     * 
     * @return true if refresh was initiated, false if unable to refresh (no coordinates)
     */
    fun refreshWeatherData(): Boolean {
        // Check if we have valid coordinates to refresh
        if (currentLatitude == 0f && currentLongitude == 0f) {
            Log.w(TAG, "Cannot refresh: No coordinates available")
            return false
        }
        
        Log.d(TAG, "Refreshing weather data with lat=$currentLatitude, lng=$currentLongitude")
        _isRefreshing.value = true
        
        viewModelScope.launch {
            try {
                // Fetch fresh data using last known coordinates
                val weatherInfo = weatherRepository.getWeatherData(currentLatitude, currentLongitude)
                
                // Update UI state with new data
                _weatherState.value = WeatherState.Success(weatherInfo)
                
                // Update city name if needed
                if (weatherInfo.locationName != "Unknown location" && weatherInfo.locationName.isNotEmpty()) {
                    _currentCity.value = weatherInfo.locationName
                }
                
                Log.d(TAG, "Refresh completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing weather data", e)
                // Don't update state to Error on refresh failure to keep displaying old data
                // Just show a message if needed (handled by the UI)
            } finally {
                _isRefreshing.value = false
            }
        }
        
        return true
    }
    
    // Search for cities using OpenWeatherMap Geocoding API
    fun searchCities(query: String): List<CitySearchResult> {
        return try {
            Log.d(TAG, "Searching for cities with query: $query")
            val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
            val url = URL("https://api.openweathermap.org/geo/1.0/direct?q=$encodedQuery&limit=5&appid=$apiKey")
            
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000 // 10 seconds timeout
            connection.readTimeout = 10000
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "City search response: $response")
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
            
            Log.d(TAG, "Found ${cities.size} cities")
            cities
        } catch (e: Exception) {
            Log.e(TAG, "Error searching for cities", e)
            emptyList()
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weatherInfo: WeatherInfo) : WeatherState()
    data class Error(val message: String) : WeatherState()
} 