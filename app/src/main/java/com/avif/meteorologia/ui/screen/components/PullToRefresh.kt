package com.avif.meteorologia.ui.screen.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Velocity
import com.avif.meteorologia.ui.theme.ColorLightBlue
import kotlin.math.roundToInt

private const val REFRESH_TRIGGER_DISTANCE = 80f // Distance needed to trigger refresh

@Composable
fun CustomPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val refreshTriggerPx = with(LocalDensity.current) { REFRESH_TRIGGER_DISTANCE.dp.toPx() }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isRefreshTriggered by remember { mutableStateOf(false) }

    // Animate offsetY back to 0 when refreshing state changes
    LaunchedEffect(isRefreshing) {
        if (isRefreshing || (!isRefreshing && isRefreshTriggered)) {
            animate(
                initialValue = offsetY,
                targetValue = 0f,
                animationSpec = tween(300)
            ) { value, _ ->
                offsetY = value
            }
            isRefreshTriggered = false
        }
    }

    // Nested scroll connection to detect pull gesture
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If we're refreshing, don't allow scrolling
                if (isRefreshing) return Offset.Zero

                // If user is scrolling down and we're at the top, handle the drag
                if (available.y < 0 && source == NestedScrollSource.UserInput) {
                    val newOffset = (offsetY + available.y).coerceAtLeast(0f)
                    val consumed = offsetY - newOffset
                    offsetY = newOffset
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If we're refreshing, don't allow scrolling
                if (isRefreshing) return Offset.Zero

                // If user is scrolling down and not all scroll consumed, handle the drag
                if (available.y > 0 && source == NestedScrollSource.UserInput) {
                    // Apply resistance as we pull down
                    val newOffset = offsetY + available.y * 0.5f
                    offsetY = newOffset
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                // If we pulled past the threshold, trigger refresh
                if (offsetY > refreshTriggerPx && !isRefreshing) {
                    isRefreshTriggered = true
                    onRefresh()
                }
                
                // Animate back to 0 if not refreshing
                if (!isRefreshing) {
                    animate(
                        initialValue = offsetY,
                        targetValue = 0f,
                        animationSpec = tween(300)
                    ) { value, _ ->
                        offsetY = value
                    }
                }
                
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // Content with vertical offset
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.roundToInt()) }
        ) {
            content()
        }
        
        // Pull indicator
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
                .align(Alignment.TopCenter)
                .offset { IntOffset(0, (offsetY - 40).roundToInt()) }
                .alpha(
                    if (isRefreshing) 1f else {
                        // Fade in as we pull down
                        (offsetY / refreshTriggerPx).coerceIn(0f, 1f)
                    }
                ),
            color = ColorLightBlue,
            strokeWidth = 2.dp
        )
    }
} 