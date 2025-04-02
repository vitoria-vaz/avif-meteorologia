package com.avif.meteorologia.data

import android.util.Log
import com.avif.meteorologia.data.remote.RemoteDataSource
import com.avif.meteorologia.data.remote.response.WeatherDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.serialization.JsonConvertException
import javax.inject.Inject

class KtorRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient
) : RemoteDataSource {
    companion object {
        private const val TAG = "KtorRemoteDataSource"
        // Use a sample API key. In a real app, you would use BuildConfig or a secrets manager
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5"
        // Sign up for a free key at https://openweathermap.org/api
        private const val API_KEY = "2d1f3afcd57e858c93ded3adebcbebfa" // Updated API key
    }

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {
        try {
            Log.d(TAG, "Fetching weather for lat=$lat, lng=$lng")
            val response = httpClient
                .get("${BASE_URL}/weather?lat=$lat&lon=$lng&appid=$API_KEY&units=metric")
                .body<WeatherDataResponse>()
            Log.d(TAG, "Weather response received: ${response.cod}, city: ${response.name}")
            return response
        } catch (e: ClientRequestException) {
            // Handle HTTP errors like 401, 404, etc.
            Log.e(TAG, "API error: ${e.response.status}", e)
            try {
                val errorBody = e.response.body<WeatherDataResponse>()
                Log.e(TAG, "API error body: ${errorBody.message}")
                return errorBody
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to parse error response", e2)
                return WeatherDataResponse(
                    cod = e.response.status.value,
                    message = "HTTP Error: ${e.message}"
                )
            }
        } catch (e: JsonConvertException) {
            // Handle JSON parsing errors
            Log.e(TAG, "JSON parsing error", e)
            return WeatherDataResponse(
                cod = 500,
                message = "Error parsing response: ${e.message}"
            )
        } catch (e: Exception) {
            // Handle other exceptions
            Log.e(TAG, "Network error", e)
            return WeatherDataResponse(
                cod = 500,
                message = "Network error: ${e.message}"
            )
        }
    }
}