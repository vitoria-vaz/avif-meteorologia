package com.avif.meteorologia.ui.screen.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.customShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 4.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    alpha: Float = 0.3f,
    spread: Dp = 0f.dp
) = drawBehind {
    val transparentColor = color.copy(alpha = 0.0f).toArgb()
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val spreadPixel = spread.toPx()
    val shadowRadiusPixel = shadowRadius.toPx()
    val borderRadiusPixel = borderRadius.toPx()
    this.drawIntoCanvas {
        val paint = Paint()
        paint.asFrameworkPaint().apply {
            this.color = transparentColor
            setShadowLayer(
                shadowRadiusPixel,
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )
        }
        it.drawRoundRect(
            left = 0f + spreadPixel,
            top = 0f + spreadPixel,
            right = this.size.width - spreadPixel,
            bottom = this.size.height - spreadPixel,
            radiusX = borderRadiusPixel,
            radiusY = borderRadiusPixel,
            paint = paint
        )
    }
} 