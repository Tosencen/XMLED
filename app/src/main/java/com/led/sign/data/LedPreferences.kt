package com.led.sign.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.led.sign.model.LedConfig
import com.led.sign.model.ScrollDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "led_settings")

class LedPreferences(private val context: Context) {

    companion object {
        private val KEY_TEXT = stringPreferencesKey("text")
        private val KEY_TEXT_COLOR = intPreferencesKey("text_color")
        private val KEY_BG_COLOR = intPreferencesKey("bg_color")
        private val KEY_FONT_SIZE = floatPreferencesKey("font_size")
        private val KEY_SCROLL_SPEED = floatPreferencesKey("scroll_speed")
        private val KEY_SCROLL_DIRECTION = stringPreferencesKey("scroll_direction")
        private val KEY_BLINK_ENABLED = booleanPreferencesKey("blink_enabled")
        private val KEY_BLINK_FREQUENCY = floatPreferencesKey("blink_frequency")
        private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        private val KEY_FOLLOW_SYSTEM = booleanPreferencesKey("follow_system")
        private val KEY_SEED_COLOR = intPreferencesKey("seed_color")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        private val KEY_SHOW_EMOJI = booleanPreferencesKey("show_emoji")
    }

    val configFlow: Flow<LedConfig> = context.dataStore.data.map { prefs ->
        LedConfig(
            text = LedConfig.DEFAULT_TEXT,
            textColor = prefs[KEY_TEXT_COLOR] ?: LedConfig.DEFAULT_TEXT_COLOR,
            bgColor = prefs[KEY_BG_COLOR] ?: LedConfig.DEFAULT_BG_COLOR,
            fontSize = prefs[KEY_FONT_SIZE] ?: LedConfig.DEFAULT_FONT_SIZE,
            scrollSpeed = prefs[KEY_SCROLL_SPEED] ?: LedConfig.DEFAULT_SCROLL_SPEED,
            scrollDirection = try {
                ScrollDirection.valueOf(prefs[KEY_SCROLL_DIRECTION] ?: LedConfig.DEFAULT_SCROLL_DIRECTION.name)
            } catch (_: Exception) { LedConfig.DEFAULT_SCROLL_DIRECTION },
            blinkEnabled = prefs[KEY_BLINK_ENABLED] ?: LedConfig.DEFAULT_BLINK_ENABLED,
            blinkFrequency = prefs[KEY_BLINK_FREQUENCY] ?: LedConfig.DEFAULT_BLINK_FREQUENCY
        )
    }

    val darkThemeFlow: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        val followSystem = prefs[KEY_FOLLOW_SYSTEM] ?: true
        if (followSystem) null else (prefs[KEY_DARK_THEME] ?: true)
    }

    val followSystemFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_FOLLOW_SYSTEM] ?: true
    }

    val seedColorFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_SEED_COLOR] ?: 0xFF006D3E.toInt()
    }

    val dynamicColorFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DYNAMIC_COLOR] ?: false
    }

    val highContrastFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HIGH_CONTRAST] ?: false
    }

    suspend fun saveConfig(config: LedConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TEXT_COLOR] = config.textColor
            prefs[KEY_BG_COLOR] = config.bgColor
            prefs[KEY_FONT_SIZE] = config.fontSize
            prefs[KEY_SCROLL_SPEED] = config.scrollSpeed
            prefs[KEY_SCROLL_DIRECTION] = config.scrollDirection.name
            prefs[KEY_BLINK_ENABLED] = config.blinkEnabled
            prefs[KEY_BLINK_FREQUENCY] = config.blinkFrequency
        }
    }

    val showEmojiFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_EMOJI] ?: false
    }

    suspend fun saveShowEmoji(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_EMOJI] = enabled
        }
    }

    suspend fun saveDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = isDark
            prefs[KEY_FOLLOW_SYSTEM] = false
        }
    }

    suspend fun saveFollowSystem(follow: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FOLLOW_SYSTEM] = follow
        }
    }

    suspend fun saveSeedColor(color: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SEED_COLOR] = color
        }
    }

    suspend fun saveDynamicColor(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun saveHighContrast(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HIGH_CONTRAST] = enabled
        }
    }
}
