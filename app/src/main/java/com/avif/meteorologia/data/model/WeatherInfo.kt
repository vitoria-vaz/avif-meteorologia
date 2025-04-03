package com.avif.meteorologia.data.model

data class WeatherInfo(
    val locationName: String,
    val conditionIcon: String,
    val condition: String,
    val temperature: Int,
    val dayOfWeek: String,
    val isDay: Boolean,
    // Additional data fields
    val humidity: Int,          // Humidity percentage
    val windSpeed: Double,      // Wind speed in m/s
    val pressure: Int,          // Atmospheric pressure in hPa
    val visibility: Int,        // Visibility in meters
    val feelsLike: Double,      // Feels like temperature
    val clouds: Int,            // Cloudiness percentage
    val rainProbability: Int = 0, // Rain probability in % (not directly from API)
    val uvIndex: Int = 0        // UV Index (not directly from API)
) {
    override fun toString(): String {
        return "WeatherInfo(location='$locationName', condition='$condition', temperature=$temperature°C, wind=${windSpeed}m/s, humidity=$humidity%)"
    }
}