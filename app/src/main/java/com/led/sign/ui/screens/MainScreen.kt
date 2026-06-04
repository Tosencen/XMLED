package com.led.sign.ui.screens

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.led.sign.model.LedConfig
import com.led.sign.model.ScrollDirection
import com.led.sign.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    config: LedConfig,
    onConfigChange: (LedConfig) -> Unit,
    isDarkTheme: Boolean,
    seedColor: Color,
    dynamicColor: Boolean,
    highContrast: Boolean,
    showEmoji: Boolean,
    onSeedColorChange: (Color) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleFollowSystem: () -> Unit,
    onShowEmojiChange: (Boolean) -> Unit,
    isFollowSystem: Boolean
) {
    val context = LocalContext.current
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // 主题按钮
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { showThemeDialog = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Palette, contentDescription = "主题", 
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                // 设置按钮
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { showSettingsDialog = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Settings, contentDescription = "设置", 
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                // 全屏预览按钮
                ExtendedFloatingActionButton(
                    onClick = {
                        val intent = Intent(context, PreviewActivity::class.java).apply {
                            putExtra("text", config.text)
                            putExtra("textColor", config.textColor)
                            putExtra("bgColor", config.bgColor)
                            putExtra("fontSize", config.fontSize)
                            putExtra("scrollSpeed", config.scrollSpeed)
                            putExtra("scrollDirection", config.scrollDirection.name)
                            putExtra("blinkEnabled", config.blinkEnabled)
                            putExtra("blinkFrequency", config.blinkFrequency)
                        }
                        context.startActivity(intent)
                    },
                    icon = { Icon(Icons.Default.Fullscreen, contentDescription = null) },
                    text = { Text("全屏预览", fontWeight = FontWeight.SemiBold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 预览区 - 占1/2
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    config.bgColorCompose(),
                                    config.bgColorCompose().copy(alpha = 0.9f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = config.text.ifEmpty { "预览区域" },
                        color = config.textColorCompose(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 5,
                        lineHeight = 48.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                    )
                }
            }

            // 输入区 - 占1/2
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    OutlinedTextField(
                        value = config.text,
                        onValueChange = { onConfigChange(config.copy(text = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("文本内容") },
                        placeholder = { Text("输入显示内容，支持Emoji") },
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (showEmoji) {
                        Text("快速插入", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(12.dp))

                        val emojiList = listOf(
                            "🎉", "✨", "❤️", "🔥", "💯", "⭐", "🎵", "🌟",
                            "👍", "👋", "🚀", "💡", "🎯", "💪", "😊", "🎄",
                            "😎", "🎶", "🎤", "📱", "💻", "🎮", "🌈", "⚡"
                        )

                        for (row in 0 until 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0 until 8) {
                                    val index = row * 8 + col
                                    if (index < emojiList.size) {
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            onClick = { onConfigChange(config.copy(text = config.text + emojiList[index])) }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(text = emojiList[index], fontSize = 20.sp)
                                            }
                                        }
                                    }
                                }
                            }
                            if (row < 2) Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            config = config,
            onConfigChange = onConfigChange,
            isDarkTheme = isDarkTheme,
            showEmoji = showEmoji,
            onShowEmojiChange = onShowEmojiChange,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeDialog(
            isDarkTheme = isDarkTheme,
            isFollowSystem = isFollowSystem,
            seedColor = seedColor,
            dynamicColor = dynamicColor,
            highContrast = highContrast,
            onSeedColorChange = onSeedColorChange,
            onDynamicColorChange = onDynamicColorChange,
            onHighContrastChange = onHighContrastChange,
            onToggleTheme = onToggleTheme,
            onToggleFollowSystem = onToggleFollowSystem,
            onDismiss = { showThemeDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    config: LedConfig,
    onConfigChange: (LedConfig) -> Unit,
    isDarkTheme: Boolean,
    showEmoji: Boolean,
    onShowEmojiChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text("设置", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("滚动设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)

                Text("滚动方向", style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScrollDirection.entries.forEach { dir ->
                        val selected = config.scrollDirection == dir
                        val animatedColor by animateColorAsState(
                            targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer 
                                         else MaterialTheme.colorScheme.surfaceVariant,
                            label = "color"
                        )
                        
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = animatedColor,
                            onClick = { onConfigChange(config.copy(scrollDirection = dir)) }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    when(dir) {
                                        ScrollDirection.LEFT -> "左滚"
                                        ScrollDirection.RIGHT -> "右滚"
                                        ScrollDirection.STOP -> "居中"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer 
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("滚动速度", style = MaterialTheme.typography.bodyLarge)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "${String.format("%.1f", config.scrollSpeed)}x",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Slider(
                    value = config.scrollSpeed,
                    onValueChange = { onConfigChange(config.copy(scrollSpeed = it)) },
                    valueRange = 0.5f..10f,
                    steps = 18,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary)
                            )
                        }
                    },
                    track = { sliderState ->
                        val fraction = sliderState.value / sliderState.valueRange.endInclusive
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FlashOn, contentDescription = null,
                            modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("闪烁", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = config.blinkEnabled,
                        onCheckedChange = { onConfigChange(config.copy(blinkEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                if (config.blinkEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("闪烁频率", style = MaterialTheme.typography.bodyLarge)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                "${String.format("%.1f", config.blinkFrequency)}Hz",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Slider(
                        value = config.blinkFrequency,
                        onValueChange = { onConfigChange(config.copy(blinkFrequency = it)) },
                        valueRange = 0.2f..5f,
                        steps = 23,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .shadow(8.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onPrimary)
                                )
                            }
                        },
                        track = { sliderState ->
                            val fraction = sliderState.value / sliderState.valueRange.endInclusive
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Text("字体设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("字体大小", style = MaterialTheme.typography.bodyLarge)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "${config.fontSize.toInt()}sp",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { if (config.fontSize > 16f) onConfigChange(config.copy(fontSize = config.fontSize - 4f)) },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Remove, contentDescription = "减小", modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    
                    Slider(
                        value = config.fontSize,
                        onValueChange = { onConfigChange(config.copy(fontSize = it)) },
                        valueRange = 16f..200f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(64.dp),
                        steps = 45,
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .shadow(8.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onPrimary)
                                )
                            }
                        },
                        track = { sliderState ->
                            val fraction = sliderState.value / sliderState.valueRange.endInclusive
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    )
                    
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { if (config.fontSize < 200f) onConfigChange(config.copy(fontSize = config.fontSize + 4f)) },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = "增大", modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Text("颜色设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = config.bgColorCompose()),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = config.text.ifEmpty { "LED 预览" },
                            color = config.textColorCompose(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Text("文字颜色", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(TextColors) { color ->
                        val colorInt = color.hashCode()
                        val isSelected = colorInt == config.textColor
                        
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = color,
                            onClick = { onConfigChange(config.copy(textColor = colorInt)) }
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color == Color.White || color == Color(0xFFFFFF00)) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Text("背景颜色", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(BgColors) { color ->
                        val colorInt = color.hashCode()
                        val isSelected = colorInt == config.bgColor
                        
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = color,
                            onClick = { onConfigChange(config.copy(bgColor = colorInt)) }
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color == Color.White || color == Color(0xFFFFFF00)) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // 快速插入开关
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEmotions, contentDescription = null,
                            modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("快速插入表情", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = showEmoji,
                        onCheckedChange = { onShowEmojiChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // 版本信息
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "XMLED v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "GitHub",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://github.com/Tosencen/XMLED")
                            )
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var isChecking by remember { mutableStateOf(false) }
                    var updateMessage by remember { mutableStateOf("") }

                    Button(
                        onClick = {
                            isChecking = true
                            updateMessage = ""
                        },
                        enabled = !isChecking,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("检查更新", color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }

                    if (updateMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            updateMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LaunchedEffect(isChecking) {
                        if (isChecking) {
                            val update = com.led.sign.update.UpdateChecker.checkUpdate()
                            if (update != null) {
                                updateMessage = "发现新版本 v${update.version}，正在下载..."
                                com.led.sign.update.UpdateChecker.downloadAndInstall(context, update.downloadUrl)
                                updateMessage = ""
                            } else {
                                updateMessage = "当前已是最新版本"
                            }
                            isChecking = false
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("完成", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDialog(
    isDarkTheme: Boolean,
    isFollowSystem: Boolean,
    seedColor: Color,
    dynamicColor: Boolean,
    highContrast: Boolean,
    onSeedColorChange: (Color) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleFollowSystem: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text("主题与色彩", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 外观模式
                Text("外观", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = if (!isDarkTheme) MaterialTheme.colorScheme.primaryContainer 
                               else MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { if (isDarkTheme) onToggleTheme() }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (!isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer 
                                      else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "浅色",
                                fontSize = 13.sp,
                                fontWeight = if (!isDarkTheme) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDarkTheme) MaterialTheme.colorScheme.primaryContainer 
                               else MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { if (!isDarkTheme) onToggleTheme() }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = if (isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer 
                                      else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "深色",
                                fontSize = 13.sp,
                                fontWeight = if (isDarkTheme) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDarkTheme) MaterialTheme.colorScheme.onPrimaryContainer 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 跟随系统
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.SettingsBrightness,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("跟随系统", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = isFollowSystem,
                        onCheckedChange = { onToggleFollowSystem() },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 主题颜色选择
                Text("主题颜色", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                
                // 颜色网格 - 4x3
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (row in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                if (index < SeedColors.size) {
                                    val (color, name) = SeedColors[index]
                                    val isSelected = seedColor == color
                                    
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        color = color,
                                        onClick = { onSeedColorChange(color) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 动态颜色
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("动态颜色", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = dynamicColor,
                        onCheckedChange = { onDynamicColorChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                // 高对比度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Contrast,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("高对比度", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = highContrast,
                        onCheckedChange = { onHighContrastChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("完成", fontWeight = FontWeight.Bold)
            }
        }
    )
}
