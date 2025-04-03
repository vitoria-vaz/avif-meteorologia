package com.avif.meteorologia.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.ui.WeatherState
import com.avif.meteorologia.ui.WeatherViewModel
import com.avif.meteorologia.ui.screen.components.ActionBar
import com.avif.meteorologia.ui.screen.components.AirQuality
import com.avif.meteorologia.ui.screen.components.CustomPullToRefresh
import com.avif.meteorologia.ui.screen.components.DailyForecast
import com.avif.meteorologia.ui.screen.components.SearchLocationDialog
import com.avif.meteorologia.ui.screen.components.WeeklyForecast
import com.avif.meteorologia.ui.theme.Cloudy_ColorGradient1
import com.avif.meteorologia.ui.theme.Cloudy_ColorGradient2
import com.avif.meteorologia.ui.theme.Cloudy_ColorGradient3
import com.avif.meteorologia.ui.theme.ColorLightBlue
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
  val isUpdatingLocation by viewModel.isUpdatingLocation.collectAsState()
  val isUsingGPS by viewModel.isUsingGPS.collectAsState()
  val isRefreshing by viewModel.isRefreshing.collectAsState()

  // State for search dialog
  var showSearchDialog by remember { mutableStateOf(false) }

  // State for refresh error
  var refreshError by remember { mutableStateOf<String?>(null) }

  // For showing refresh errors
  val snackbarHostState = remember { SnackbarHostState() }
  LocalContext.current

  // Show error message when refresh fails
  LaunchedEffect(refreshError) {
    refreshError?.let {
      snackbarHostState.showSnackbar(it)
      refreshError = null
    }
  }

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
    containerColor = Color.Transparent,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { paddings ->
    // Choose background gradient based on weather condition
    val brush = when {
      // Condition-based gradients
      weatherState is WeatherState.Success -> {
        val weatherInfo = (weatherState as WeatherState.Success).weatherInfo
        when {
          // Simplified condition checking
          weatherInfo.condition.contains("rain", ignoreCase = true) -> {
            Brush.verticalGradient(
              colors = listOf(
                Rainy_ColorGradient1,
                Rainy_ColorGradient2,
                Rainy_ColorGradient3
              )
            )
          }
          weatherInfo.condition.contains("cloud", ignoreCase = true) -> {
            Brush.verticalGradient(
              colors = listOf(
                Cloudy_ColorGradient1,
                Cloudy_ColorGradient2,
                Cloudy_ColorGradient3
              )
            )
          }
          weatherInfo.condition.contains("storm", ignoreCase = true) ||
            weatherInfo.condition.contains("thunder", ignoreCase = true) -> {
            Brush.verticalGradient(
              colors = listOf(
                Stormy_ColorGradient1,
                Stormy_ColorGradient2,
                Stormy_ColorGradient3
              )
            )
          }
          else -> {
            // Default to sunny
            Brush.verticalGradient(
              colors = listOf(
                Sunny_ColorGradient1,
                Sunny_ColorGradient2,
                Sunny_ColorGradient3
              )
            )
          }
        }
      }
      // Default gradient for loading/error states
      else -> {
        Brush.verticalGradient(
          colors = listOf(
            Sunny_ColorGradient1,
            Sunny_ColorGradient2,
            Sunny_ColorGradient3
          )
        )
      }
    }

    when (weatherState) {
      is WeatherState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(paddings),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = ColorLightBlue)
        }
      }
      is WeatherState.Success -> {
        val weatherInfo = (weatherState as WeatherState.Success).weatherInfo

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(paddings)
        ) {
          // Use CustomPullToRefresh instead of PullToRefresh
          CustomPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { 
              val success = viewModel.refreshWeatherData()
              if (!success) {
                refreshError = "Unable to refresh. No location data."
              }
            },
            modifier = Modifier.fillMaxSize()
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
            ) {
              Spacer(modifier = Modifier.height(8.dp))

              // Top action bar with location name and GPS indicator
              ActionBar(
                locationName = currentCity,
                isUpdatingLocation = isUpdatingLocation,
                isUsingGPS = isUsingGPS,
                onLocationClick = { showSearchDialog = true }
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Daily forecast component
              DailyForecast(weatherInfo = weatherInfo)

              Spacer(modifier = Modifier.height(16.dp))

              // Air quality component
              AirQuality(
                weatherInfo = weatherInfo
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Weekly forecast component
              WeeklyForecast()

              Spacer(modifier = Modifier.height(24.dp))
            }
          }
        }
      }
      is WeatherState.Error -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(paddings),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
          ) {
            Text(
              text = "Unable to load weather data",
              style = MaterialTheme.typography.bodyLarge,
              color = White,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = (weatherState as WeatherState.Error).message,
              style = MaterialTheme.typography.bodyMedium,
              color = ColorTextSecondary,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  }
} 