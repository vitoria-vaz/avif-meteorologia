package androidlead.weatherappui

import android.os.Bundle
import androidlead.weatherappui.ui.screen.WeatherScreen
import androidlead.weatherappui.ui.theme.WeatherAppUiTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

val weatherType = 3


// MODIFICAR -> criar classe para a previsao,
// seu int e string descritiva

fun getForecastString(weatherType: Int): String {
    return when (weatherType) {
        1 -> "Sunny"
        2 -> "Cloudy"
        3 -> "Rainy"
        4 -> "Stormy"
        else -> "Unknown"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppUiTheme {
                WeatherScreen(weatherType)
            }
        }
    }
}
