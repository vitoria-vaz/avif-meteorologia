package com.avif.meteorologia.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDataResponse(
    val coord: Coord? = null,
    val weather: List<Weather> = emptyList(),
    val base: String? = null,
    val main: Main? = null,
    val visibility: Int? = null,
    val wind: Wind? = null,
    val rain: Rain? = null,
    val clouds: Clouds? = null,
    val dt: Long? = null,
    val sys: Sys? = null,
    val timezone: Int? = null,
    val id: Long? = null,
    val name: String = "",
    val cod: Int = 200,
    val message: String? = null
)

@Serializable
data class Main(
    val temp: Double = 0.0,
    @SerialName("feels_like") val feelsLike: Double = 0.0,
    @SerialName("temp_min") val tempMin: Double = 0.0,
    @SerialName("temp_max") val tempMax: Double = 0.0,
    val pressure: Int = 1013,
    val humidity: Int = 0,
    @SerialName("sea_level") val seaLevel: Int? = null,
    @SerialName("grnd_level") val groundLevel: Int? = null
)

@Serializable
data class Sys(
    val type: Int? = null,
    val id: Int? = null,
    val country: String = "",
    val sunrise: Long = 0,
    val sunset: Long = 0
)