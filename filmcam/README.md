# Filmcam - Google I/O 2026 Compose First Camera App

A cutting-edge Android camera application built with the latest **Google I/O 2026** technologies and **Compose First** architecture.

## 🎬 Features

### Neural Expressive Design (Material 3 1.5-alpha)
- **Tonal Fluid Palette**: Organic, shifting dark backgrounds using expressive surface tokens
- **Expressive Type Scale**: Cinematic displayLarge typography with mandatory 16sp accessibility baseline
- **Control Deck Elevation**: Automatic visual lift using Material 3 theme math

### Live View, Zero Obstruction Architecture
- **Shared Element Transitions**: Viewfinder morphs seamlessly between capture and tuning states
- **Physics-based Spring Animations**: Bouncy, organic transitions using Compose 1.12 spring APIs
- **Edge-to-Edge Display**: Full immersive camera experience

### Flexbox Master Control Deck
- **Dual Pipeline System**: Toggle between Simulation and LUT workflows
- **AnimatedContent Scaffolding**: Hardware-accelerated crossfades between control sets
- **Context-Aware Interface**: Adaptive bottom 40% deck swapping entire toolkits

### Tactile Custom Components
- **Snapping Film Carousel**: LazyRow with mechanical snap-to-center behavior
- **Luma Curve Canvas**: Three-node interactive contrast curve with drag gestures
- **Neural Haptic Integration**: Precise haptic feedback on threshold crossings

### Expressive Processing Indicators
- **Wave Progress Component**: Multi-layered pulsing waveform for RAW processing
- **Infinite Transition Animations**: Breathing amplitude effects

## 🛠️ Tech Stack

| Technology | Version |
|------------|---------|
| Kotlin | 2.0.21 (K2 Compiler) |
| Android Gradle Plugin | 8.7.0 |
| Gradle | 8.9 |
| Compile SDK | 35 (Android 15) |
| Jetpack Compose | 2024.12.01 BOM |
| Material 3 | 1.3.1 + 1.5-alpha features |
| Compose Navigation | 2.8.4 |
| CameraX | 1.4.0 |

## 📁 Project Structure

```
filmcam/
├── app/
│   ├── src/main/
│   │   ├── java/com/filmcam/app/
│   │   │   ├── domain/model/
│   │   │   │   └── PipelineModels.kt      # ActivePipeline, FilmSimulation, ColorSpace
│   │   │   ├── ui/
│   │   │   │   ├── animation/
│   │   │   │   │   └── SpringAnimations.kt # Physics-based spring tokens
│   │   │   │   ├── component/
│   │   │   │   │   ├── CurveCanvas.kt      # Luma curve with haptics
│   │   │   │   │   ├── FilmCarousel.kt     # Snapping film selector
│   │   │   │   │   └── WaveProgress.kt     # Expressive wave indicator
│   │   │   │   ├── screen/
│   │   │   │   │   └── CameraScreen.kt     # Main camera UI
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt            # Neural expressive palette
│   │   │   │       ├── Theme.kt            # Material 3 theme setup
│   │   │   │       └── Type.kt             # Expressive type scale
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml                  # Version catalog
│   └── wrapper/
├── build.gradle.kts
└── settings.gradle.kts
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android SDK 35
- Physical device or emulator with camera support

### Installation
1. Open the project in Android Studio
2. Sync Gradle files
3. Run on a device/emulator with API 26+
4. Grant camera permission when prompted

## 🎯 Key Implementation Highlights

### Phase 1: Neural Expressive Theming
```kotlin
// Theme.kt - Tonal Fluid Palette
surfaceContainer = SurfaceContainerLowest // Color(0xFF0F0F0F)
surfaceContainerHigh // Automatic elevation lift
```

### Phase 2: Shared Element & Viewfinder Morphing
```kotlin
// CameraScreen.kt
SharedTransitionLayout {
    AnimatedContent(targetState = activePipeline) { pipeline ->
        // Viewfinder morphs: fillMaxSize() → 60% height + 32.dp corners
    }
}
```

### Phase 3: Flexbox Master Control Deck
```kotlin
SingleChoiceSegmentedButtonRow {
    // Simulation ↔ LUT toggle
}
AnimatedContent(targetState = activePipeline) {
    // Branch-specific controls
}
```

### Phase 4: Tactile Custom Components
```kotlin
// FilmCarousel.kt - Snap fling behavior
LazyRow(state = listState, contentPadding = PaddingValues(horizontal = 64.dp))

// CurveCanvas.kt - Drag gestures with haptics
detectDragGestures { change, dragAmount ->
    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}
```

### Phase 5: Expressive Processing Indicators
```kotlin
// WaveProgress.kt - Multi-layered waveform
infiniteRepeatable(animation = tween(durationMillis = 1200))
```

## 📱 Screenshots

The app features:
- Full-screen camera viewfinder with morphing capability
- Bottom control deck with segmented pipeline toggle
- Film simulation carousel with snap-to-center physics
- Interactive luma curve editor with three draggable nodes
- Pulsing wave progress indicator during processing

## 📄 License

This project demonstrates Google I/O 2026 Compose First architecture patterns.
