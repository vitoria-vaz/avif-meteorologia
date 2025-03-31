package com.avif.meteorologia.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.ui.WeatherState
import com.avif.meteorologia.ui.WeatherViewModel
import com.avif.meteorologia.ui.screen.components.ActionBar
import com.avif.meteorologia.ui.screen.components.AirQuality
import com.avif.meteorologia.ui.screen.components.DailyForecast
import com.avif.meteorologia.ui.screen.components.SearchLocationDialog
import com.avif.meteorologia.ui.screen.components.WeeklyForecast
import com.avif.meteorologia.ui.theme.Cloudy_ColorGradient1
import com.avif.meteorologia.ui.theme.Cloudy_ColorGradient2
import com.avif.meteorologia.ui.theme.Cloudy_ColorGradient3
import com.avif.meteorologia.ui.theme.ColorBackground
import com.avif.meteorologia.ui.theme.ColorLightBlue
import com.avif.meteorologia.ui.theme.ColorText
import com.avif.meteorologia.ui.theme.ColorTextSecondary
import com.avif.meteorologia.ui.theme.Rainy_ColorGradient1
import com.avif.meteorologia.ui.theme.Rainy_ColorGradient2
import com.avif.meteorologia.ui.theme.Rainy_ColorGradient3
import com.avif.meteorologia.ui.theme.Stormy_ColorGradient1
import com.avif.meteorologia.ui.theme.Stormy_ColorGradient2
import com.avif.meteorologia.ui.theme.Stormy_ColorGradient3
import com.avif.meteorologia.ui.theme.Sunny_ColorGradient1
import com.avif.meteorologia.ui.theme.Sunny_ColorGradient2
import com.avif.meteorologia.ui.theme.Sunny_ColorGradient3
import com.avif.meteorologia.ui.theme.White

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weatherState by viewModel.weatherState.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    
    // State for search dialog
    var showSearchDialog by remember { mutableStateOf(false) }
    
    // Show search dialog when requested
    if (showSearchDialog) {
        SearchLocationDialog(
            onDismiss = { showSearchDialog = false },
            onCitySelected = { city -> 
                viewModel.updateCity(city)
            },
            onSearch = { query -> viewModel.searchCities(query) }
        )
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { paddings ->
        when (weatherState) {
            is WeatherState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorBackground)  // Use theme color instead of hardcoded value
                        .padding(paddings),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(0.4f))
                    CircularProgressIndicator(color = ColorLightBlue)  // Use theme color
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading weather data...",
                        color = ColorText,  // Use theme color
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(0.6f))
                }
            }
            is WeatherState.Success -> {
                val weatherInfo = (weatherState as WeatherState.Success).weatherInfo
                
                // Determine weather type based on condition icon
                val weatherType = when {
                    weatherInfo.conditionIcon.startsWith("01") -> 1 // Clear/Sunny
                    weatherInfo.conditionIcon.startsWith("02") || 
                    weatherInfo.conditionIcon.startsWith("03") || 
                    weatherInfo.conditionIcon.startsWith("04") -> 2 // Cloudy
                    weatherInfo.conditionIcon.startsWith("09") || 
                    weatherInfo.conditionIcon.startsWith("10") -> 3 // Rainy
                    weatherInfo.conditionIcon.startsWith("11") -> 4 // Thunderstorm/Stormy
                    else -> 2 // Default to cloudy
                }
                
                // Choose gradient colors based on weatherType
                val gradientColors = when (weatherType) {
                    1 -> listOf(Sunny_ColorGradient1, Sunny_ColorGradient2, Sunny_ColorGradient3)
                    2 -> listOf(Cloudy_ColorGradient1, Cloudy_ColorGradient2, Cloudy_ColorGradient3)
                    3 -> listOf(Rainy_ColorGradient1, Rainy_ColorGradient2, Rainy_ColorGradient3)
                    4 -> listOf(Stormy_ColorGradient1, Stormy_ColorGradient2, Stormy_ColorGradient3)
                    else -> listOf(White, White, White)
                }
                
                val brush = Brush.verticalGradient(gradientColors)
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush)
                        .verticalScroll(rememberScrollState())
                        .padding(paddings)
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    ActionBar(
                        locationName = currentCity,
                        onLocationClick = { showSearchDialog = true }
                    )
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                    DailyForecast(weatherInfo = weatherInfo)
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                    AirQuality()
                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                    WeeklyForecast()
                }
            }
            is WeatherState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorBackground)  // Use theme color
                        .padding(paddings)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(0.4f))
                    Text(
                        text = "Unable to load weather data",
                        color = ColorText,  // Use theme color
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (weatherState as WeatherState.Error).message,
                        color = ColorTextSecondary,  // Use theme color
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(0.6f))
                }
            }
        }
    }
} 