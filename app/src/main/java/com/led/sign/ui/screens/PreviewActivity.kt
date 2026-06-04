package com.led.sign.ui.screens

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.led.sign.model.LedConfig
import com.led.sign.model.ScrollDirection
import com.led.sign.ui.components.LedSignView
import com.led.sign.ui.theme.LEDSignTheme
import kotlinx.coroutines.delay

class PreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)

        val config = LedConfig(
            text = intent.getStringExtra("text") ?: "Hello LED!",
            textColor = intent.getIntExtra("textColor", 0xFF00FF00.toInt()),
            bgColor = intent.getIntExtra("bgColor", 0xFF1A1A2E.toInt()),
            fontSize = intent.getFloatExtra("fontSize", 48f),
            scrollSpeed = intent.getFloatExtra("scrollSpeed", 3f),
            scrollDirection = try {
                ScrollDirection.valueOf(intent.getStringExtra("scrollDirection") ?: "LEFT")
            } catch (_: Exception) { ScrollDirection.LEFT },
            blinkEnabled = intent.getBooleanExtra("blinkEnabled", false),
            blinkFrequency = intent.getFloatExtra("blinkFrequency", 1f)
        )

        setContent {
            LEDSignTheme(darkTheme = true) {
                var showControls by remember { mutableStateOf(false) }
                var currentFontSize by remember { mutableFloatStateOf(config.fontSize) }

                // 4秒后自动隐藏控制栏
                LaunchedEffect(showControls) {
                    if (showControls) {
                        delay(4000)
                        showControls = false
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showControls = !showControls }
                ) {
                    LedSignView(
                        config = config.copy(fontSize = currentFontSize),
                        modifier = Modifier.fillMaxSize(),
                        isFullScreen = true
                    )

                    // 控制栏
                    AnimatedVisibility(
                        visible = showControls,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                                // 返回按钮
                                Surface(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .clickable { finish() },
                                    shape = CircleShape,
                                    color = Color.Transparent
                                ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "返回",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // 字号调节
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable { if (currentFontSize > 16f) currentFontSize -= 4f },
                                    shape = CircleShape,
                                    color = Color.Transparent
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = "减小字号",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "${currentFontSize.toInt()}sp",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )

                                Surface(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable { if (currentFontSize < 200f) currentFontSize += 4f },
                                    shape = CircleShape,
                                    color = Color.Transparent
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "增大字号",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
