package com.led.sign.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.led.sign.model.LedConfig
import com.led.sign.model.ScrollDirection

@Composable
fun LedSignView(
    config: LedConfig,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false
) {
    val density = LocalDensity.current

    val textColorArgb = config.textColorCompose().toArgb()
    val bgColor = config.bgColorCompose()

    val fontSizePx = if (isFullScreen) {
        with(density) { (config.fontSize * 3.0f).dp.toPx() }
    } else {
        with(density) { config.fontSize.dp.toPx() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "led")

    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (500000f / config.scrollSpeed.coerceAtLeast(0.1f)).toInt(),
                easing = LinearEasing
            )
        ),
        label = "scroll"
    )

    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (500f / config.blinkFrequency.coerceAtLeast(0.1f)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    val alpha = if (config.blinkEnabled) blinkAlpha else 1f
    val ledDotSpacing = if (isFullScreen) 8f else 6f
    val ledDotRadius = if (isFullScreen) 2.2f else 1.8f

    val dotPaint = remember {
        Paint().apply {
            color = android.graphics.Color.argb(25, 255, 255, 255)
            style = Paint.Style.FILL
        }
    }

    val textPaint = remember(fontSizePx, textColorArgb) {
        Paint().apply {
            color = textColorArgb
            textSize = fontSizePx
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(12f, 0f, 0f, textColorArgb)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas

            // 绘制LED点阵背景
            var y = 0f
            while (y < canvasHeight) {
                var x = 0f
                while (x < canvasWidth) {
                    native.drawCircle(x, y, ledDotRadius, dotPaint)
                    x += ledDotSpacing
                }
                y += ledDotSpacing
            }

            val text = config.text.ifEmpty { " " }
            textPaint.alpha = (alpha * 255).toInt()

            // 处理多行文本
            val lines = text.split("\n")
            val lineHeight = fontSizePx * 1.3f
            val totalTextHeight = lines.size * lineHeight
            val startY = (canvasHeight - totalTextHeight) / 2f + lineHeight * 0.8f

            native.save()
            native.clipRect(0f, 0f, canvasWidth, canvasHeight)

            when (config.scrollDirection) {
                ScrollDirection.STOP -> {
                    lines.forEachIndexed { index, line ->
                        if (line.isNotEmpty()) {
                            val lineWidth = textPaint.measureText(line)
                            val xPos = (canvasWidth - lineWidth) / 2f
                            val yPos = startY + index * lineHeight
                            native.drawText(line, xPos, yPos, textPaint)
                        }
                    }
                }
                ScrollDirection.LEFT -> {
                    var maxWidth = 0f
                    lines.forEach { line ->
                        if (line.isNotEmpty()) {
                            val w = textPaint.measureText(line)
                            if (w > maxWidth) maxWidth = w
                        }
                    }
                    val gap = 120f
                    val totalWidth = maxWidth + gap

                    lines.forEachIndexed { index, line ->
                        if (line.isNotEmpty()) {
                            val rawOffset = (scrollOffset * 3f) % totalWidth
                            var x = canvasWidth - rawOffset
                            val yPos = startY + index * lineHeight
                            while (x > -totalWidth) {
                                native.drawText(line, x, yPos, textPaint)
                                x -= totalWidth
                            }
                        }
                    }
                }
                ScrollDirection.RIGHT -> {
                    var maxWidth = 0f
                    lines.forEach { line ->
                        if (line.isNotEmpty()) {
                            val w = textPaint.measureText(line)
                            if (w > maxWidth) maxWidth = w
                        }
                    }
                    val gap = 120f
                    val totalWidth = maxWidth + gap

                    lines.forEachIndexed { index, line ->
                        if (line.isNotEmpty()) {
                            val lineWidth = textPaint.measureText(line)
                            val rawOffset = (scrollOffset * 3f) % totalWidth
                            var x = -lineWidth + rawOffset
                            val yPos = startY + index * lineHeight
                            while (x < canvasWidth + totalWidth) {
                                native.drawText(line, x, yPos, textPaint)
                                x += totalWidth
                            }
                        }
                    }
                }
            }

            native.restore()
        }
    }
}
