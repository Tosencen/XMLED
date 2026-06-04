package com.led.sign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.led.sign.data.LedPreferences
import com.led.sign.model.LedConfig
import com.led.sign.ui.screens.MainScreen
import com.led.sign.ui.theme.LEDSignTheme
import com.led.sign.ui.theme.SeedColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = LedPreferences(applicationContext)

        setContent {
            var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            var followSystem by remember { mutableStateOf(true) }
            var seedColor by remember { mutableStateOf(SeedColors[0].first) }
            var dynamicColor by remember { mutableStateOf(false) }
            var highContrast by remember { mutableStateOf(false) }
            var showEmoji by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            var config by remember { mutableStateOf(LedConfig()) }

            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()

            // 只加载一次，后续由内存管理
            LaunchedEffect(Unit) {
                preferences.configFlow.first().let { saved ->
                    config = config.copy(
                        textColor = saved.textColor,
                        bgColor = saved.bgColor,
                        fontSize = saved.fontSize,
                        scrollSpeed = saved.scrollSpeed,
                        scrollDirection = saved.scrollDirection,
                        blinkEnabled = saved.blinkEnabled,
                        blinkFrequency = saved.blinkFrequency
                    )
                }
            }

            LaunchedEffect(Unit) {
                preferences.seedColorFlow.collectLatest { color ->
                    seedColor = Color(color)
                }
            }

            LaunchedEffect(Unit) {
                preferences.dynamicColorFlow.collectLatest { enabled ->
                    dynamicColor = enabled
                }
            }

            LaunchedEffect(Unit) {
                preferences.highContrastFlow.collectLatest { enabled ->
                    highContrast = enabled
                }
            }

            LaunchedEffect(Unit) {
                preferences.showEmojiFlow.collectLatest { enabled ->
                    showEmoji = enabled
                }
            }

            val finalDarkTheme = when {
                followSystem -> systemDark
                isDarkTheme != null -> isDarkTheme!!
                else -> systemDark
            }

            LEDSignTheme(
                darkTheme = finalDarkTheme,
                seedColor = seedColor,
                dynamicColor = dynamicColor,
                highContrast = highContrast
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        config = config,
                        onConfigChange = { newConfig ->
                            config = newConfig
                            scope.launch {
                                preferences.saveConfig(newConfig)
                            }
                        },
                        isDarkTheme = finalDarkTheme,
                        seedColor = seedColor,
                        dynamicColor = dynamicColor,
                        highContrast = highContrast,
                        showEmoji = showEmoji,
                        onSeedColorChange = { color ->
                            seedColor = color
                            scope.launch {
                                preferences.saveSeedColor(color.toArgb())
                            }
                        },
                        onDynamicColorChange = { enabled ->
                            dynamicColor = enabled
                            scope.launch {
                                preferences.saveDynamicColor(enabled)
                            }
                        },
                        onHighContrastChange = { enabled ->
                            highContrast = enabled
                            scope.launch {
                                preferences.saveHighContrast(enabled)
                            }
                        },
                        onToggleTheme = {
                            val newTheme = !finalDarkTheme
                            isDarkTheme = newTheme
                            followSystem = false
                            scope.launch {
                                preferences.saveDarkTheme(newTheme)
                            }
                        },
                        onToggleFollowSystem = {
                            followSystem = !followSystem
                            scope.launch {
                                preferences.saveFollowSystem(followSystem)
                            }
                        },
                        onShowEmojiChange = { enabled ->
                            showEmoji = enabled
                            scope.launch {
                                preferences.saveShowEmoji(enabled)
                            }
                        },
                        isFollowSystem = followSystem
                    )
                }
            }
        }
    }
}
