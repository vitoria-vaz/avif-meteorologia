package com.avif.meteorologia.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avif.meteorologia.R
import com.avif.meteorologia.data.model.WeatherInfo

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        contentAlignment = Alignment.Center
    ) {
        when (weatherState) {
            is WeatherState.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(50.dp)
                )
            }
            is WeatherState.Success -> {
                val weatherInfo = (weatherState as WeatherState.Success).weatherInfo
                WeatherContent(weatherInfo = weatherInfo)
            }
            is WeatherState.Error -> {
                ErrorContent(
                    message = (weatherState as WeatherState.Error).message
                )
            }
        }
    }
}

@Composable
fun WeatherContent(weatherInfo: WeatherInfo) {
    val context = LocalContext.current
    
    // Map OpenWeatherMap icon code to local drawable resource
    val iconResId = when (weatherInfo.conditionIcon) {
        "01d" -> R.drawable.ic_clear_day
        "01n" -> R.drawable.ic_clear_night
        "02d", "03d", "04d" -> R.drawable.ic_cloudy
        "02n", "03n", "04n" -> R.drawable.ic_cloudy_night
        "09d", "09n", "10d", "10n" -> R.drawable.ic_rainy
        "11d", "11n" -> R.drawable.ic_thunderstorm
        "13d", "13n" -> R.drawable.ic_snowy
        "50d", "50n" -> R.drawable.ic_foggy
        else -> R.drawable.ic_unknown
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = weatherInfo.locationName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = weatherInfo.dayOfWeek,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Weather Icon
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = weatherInfo.condition,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Temperature
            Text(
                text = "${weatherInfo.temperature}°C",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Condition
            Text(
                text = weatherInfo.condition,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red,
            textAlign = TextAlign.Center
        )
    }
} 