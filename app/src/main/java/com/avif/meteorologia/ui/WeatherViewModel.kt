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

    // Default coordinates for São Paulo, Brazil
    private val defaultLat = -23.5489f
    private val defaultLng = -46.6388f
    
    private val _currentCity = MutableStateFlow("São Paulo")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()
    
    // API key for OpenWeatherMap Geocoding API
    private val apiKey = "2d1f3afcd57e858c93ded3adebcbebfa" // Use the same API key

    init {
        fetchWeatherData()
    }

    fun fetchWeatherData(lat: Float = defaultLat, lng: Float = defaultLng) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val weatherInfo = weatherRepository.getWeatherData(lat, lng)
                _weatherState.value = WeatherState.Success(weatherInfo)
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Update current city and fetch weather
    fun updateCity(city: CitySearchResult) {
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