<div align="center">

# XMLED

**一款 Android LED 滚动字幕应用**

[![Android](https://img.shields.io/badge/Android-26%2B-brightgreen)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

</div>

---

## 功能特性

| 功能 | 说明 |
|:---|:---|
| LED 点阵效果 | Canvas 绘制真实 LED 点阵背景 |
| 滚动字幕 | 左滚、右滚、居中三种模式，无缝循环 |
| 多行文字 | 支持换行，每行独立居中 |
| Emoji 支持 | 文字内容支持 Emoji 表情 |
| 闪烁效果 | 可调节闪烁频率 |
| 全屏预览 | 横屏全屏显示，点击屏幕调节字号 |
| 主题切换 | 12 种主题色，支持深色/浅色/跟随系统 |
| 颜色自定义 | 11 种文字颜色 + 11 种背景颜色 |
| 在线更新 | 通过 GitHub Releases 检查更新 |

## 截图预览

<div align="center">

| 首页 | 主题 | 设置 | 全屏预览 |
|:---:|:---:|:---:|:---:|
| ![首页](screenshots/001.png) | ![主题](screenshots/002.png) | ![设置](screenshots/003.png) | ![全屏](screenshots/004.png) |

</div>

## 技术栈

- **语言** — Kotlin
- **UI 框架** — Jetpack Compose
- **设计规范** — Material Design 3
- **数据存储** — DataStore Preferences
- **图形渲染** — Canvas + Native Paint

## 环境要求

| 项目 | 版本 |
|:---|:---|
| Android Studio | Hedgehog+ |
| Kotlin | 2.0.0 |
| AGP | 8.5.0 |
| compileSdk | 35 |
| minSdk | 26 |

## 快速开始

```bash
# 克隆项目
git clone https://github.com/Tosencen/XMLED.git

# 用 Android Studio 打开项目

# 运行到设备或模拟器
```

## 下载安装

前往 [Releases](https://github.com/Tosencen/XMLED/releases) 页面下载最新 APK。

## 许可证

本项目采用 [MIT License](LICENSE) 开源协议。
