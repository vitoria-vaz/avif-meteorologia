package androidlead.weatherappui.ui.screen

import androidlead.weatherappui.ui.screen.components.ActionBar
import androidlead.weatherappui.ui.screen.components.AirQuality
import androidlead.weatherappui.ui.screen.components.DailyForecast
import androidlead.weatherappui.ui.screen.components.WeeklyForecast
import androidlead.weatherappui.ui.theme.*  // Import colors from the theme package
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush // Import Brush to apply gradient

@Preview
@Composable
fun WeatherScreen(weatherType: Int) {
    // Choose gradient colors based on weatherType
    val gradientColors = when (weatherType) {
        1 -> listOf(Sunny_ColorGradient1, Sunny_ColorGradient2, Sunny_ColorGradient3) // Sunny
        2 -> listOf(Cloudy_ColorGradient1, Cloudy_ColorGradient2, Cloudy_ColorGradient3) // Cloudy
        3 -> listOf(Rainy_ColorGradient1, Rainy_ColorGradient2, Rainy_ColorGradient3) // Rainy
        4 -> listOf(Stormy_ColorGradient1, Stormy_ColorGradient2, Stormy_ColorGradient3) // Stormy
        else -> listOf(White, White, White) // Default fallback (white gradient)
    }

    val brush = Brush.verticalGradient(gradientColors)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush), // Apply the gradient to the whole Scaffold
        containerColor = Color.Transparent // Ensure the background of the Scaffold is transparent
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp) // Add padding only to content
        ) {
            ActionBar()
            Spacer(modifier = Modifier.height(12.dp))
            DailyForecast(Modifier, weatherType, "Monday, 12 Feb")
            Spacer(modifier = Modifier.height(24.dp))
            AirQuality()
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyForecast()
        }
    }
}
