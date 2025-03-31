package androidlead.weatherappui.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

private val AppColorTheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)
@Preview
@Composable
fun WeatherAppUiTheme(
    content:
@Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorTheme,
        typography = Typography,
        content = content
    )
}