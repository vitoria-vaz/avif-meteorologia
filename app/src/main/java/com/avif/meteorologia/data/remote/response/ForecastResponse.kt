package com.avif.meteorologia.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val cod: String = "",
    val message: Int = 0,
    val cnt: Int = 0,
    val list: List<ForecastItem> = emptyList(),
    val city: City = City()
)

@Serializable
data class ForecastItem(
    val dt: Long = 0,
    val main: ForecastMain = ForecastMain(),
    val weather: List<Weather> = emptyList(),
    val clouds: Clouds = Clouds(),
    val wind: Wind = Wind(),
    val visibility: Int = 0,
    val pop: Double = 0.0, // Probability of precipitation
    val rain: Rain? = null, // Some items may not have rain data
    val snow: Snow? = null, // Some items may not have snow data
    @SerialName("dt_txt") val dtText: String = ""
)

@Serializable
data class ForecastMain(
    val temp: Double = 0.0,
    @SerialName("feels_like") val feelsLike: Double = 0.0,
    @SerialName("temp_min") val tempMin: Double = 0.0,
    @SerialName("temp_max") val tempMax: Double = 0.0,
    val pressure: Int = 0,
    @SerialName("sea_level") val seaLevel: Int = 0,
    @SerialName("grnd_level") val grndLevel: Int = 0,
    val humidity: Int = 0,
    @SerialName("temp_kf") val tempKf: Double = 0.0
)

@Serializable
data class City(
    val id: Long = 0,
    val name: String = "",
    val coord: Coord = Coord(),
    val country: String = "",
    val population: Int = 0,
    val timezone: Int = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0
)

@Serializable
data class Snow(
    @SerialName("3h") val threeHour: Double = 0.0
) 