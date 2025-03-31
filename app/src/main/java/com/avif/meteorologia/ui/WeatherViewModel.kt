package com.avif.meteorologia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // Default coordinates for São Paulo, Brazil
    private val defaultLat = -23.5489f
    private val defaultLng = -46.6388f

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

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
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weatherInfo: WeatherInfo) : WeatherState()
    data class Error(val message: String) : WeatherState()
} 