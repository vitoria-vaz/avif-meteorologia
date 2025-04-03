package com.avif.meteorologia.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coord(
    val lon: Double = 0.0,
    val lat: Double = 0.0
)

@Serializable
data class Weather(
    val id: Int = 800,
    val main: String = "Clear",
    val description: String = "",
    val icon: String = "01d"
)

@Serializable
data class Wind(
    val speed: Double = 0.0,
    val deg: Int = 0,
    val gust: Double? = null
)

@Serializable
data class Clouds(
    val all: Int = 0
)

@Serializable
data class Rain(
    @SerialName("1h") val oneHour: Double = 0.0,
    @SerialName("3h") val threeHour: Double = 0.0
) 