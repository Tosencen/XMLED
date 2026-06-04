package com.led.sign.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class ScrollDirection {
    LEFT, RIGHT, STOP
}

data class LedConfig(
    val text: String = DEFAULT_TEXT,
    val textColor: Int = DEFAULT_TEXT_COLOR,
    val bgColor: Int = DEFAULT_BG_COLOR,
    val fontSize: Float = DEFAULT_FONT_SIZE,
    val scrollSpeed: Float = DEFAULT_SCROLL_SPEED,
    val scrollDirection: ScrollDirection = DEFAULT_SCROLL_DIRECTION,
    val blinkEnabled: Boolean = DEFAULT_BLINK_ENABLED,
    val blinkFrequency: Float = DEFAULT_BLINK_FREQUENCY
) {
    fun textColorCompose() = Color(textColor)
    fun bgColorCompose() = Color(bgColor)

    companion object {
        const val DEFAULT_TEXT = "Hello LED!"
        val DEFAULT_TEXT_COLOR = 0xFF00FF00.toInt()
        val DEFAULT_BG_COLOR = 0xFF1A1A2E.toInt()
        const val DEFAULT_FONT_SIZE = 48f
        const val DEFAULT_SCROLL_SPEED = 1f
        val DEFAULT_SCROLL_DIRECTION = ScrollDirection.LEFT
        const val DEFAULT_BLINK_ENABLED = false
        const val DEFAULT_BLINK_FREQUENCY = 1f
    }
}
