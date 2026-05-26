# Android UI Flow - Modern Android 15+ App

A complete Android application demonstrating the latest Android development practices and technologies unveiled at recent Google I/O events.

## 🚀 Technologies Used

### Core Stack
- **Kotlin 2.0.21** - Latest Kotlin with K2 compiler
- **Android Gradle Plugin 8.7.0** - Latest stable AGP
- **Gradle 8.9** - Modern build system
- **Compile SDK 35** - Android 15/API 35

### UI & Design
- **Jetpack Compose** - Declarative UI toolkit
- **Material 3 (Material You)** - Dynamic color theming
- **Compose Navigation** - Type-safe navigation
- **Edge-to-Edge Display** - Modern fullscreen experience

### Architecture
- **MVVM-ready structure** - Clean separation of concerns
- **UI State Management** - Compose state hoisting
- **Navigation Graph** - Sealed class route definitions

## 📱 UI/UX Flow

The app demonstrates a complete user journey:

1. **Splash Screen** - Animated branding with spring animations
2. **Onboarding** - Multi-page pager with smooth transitions
3. **Login** - Material 3 form components
4. **Home Dashboard** - Scaffold with TopAppBar and cards
5. **Profile** - Parameterized navigation with user data
6. **Settings** - Interactive preferences with logout

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/uiflow/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   │   ├── Theme.kt      # Material 3 color schemes
│   │   │   │   └── Type.kt       # Typography system
│   │   │   ├── navigation/
│   │   │   │   ├── Screen.kt     # Route definitions
│   │   │   │   └── AppNavigation.kt # NavGraph
│   │   │   └── screens/
│   │   │       ├── SplashScreen.kt
│   │   │       ├── OnboardingScreen.kt
│   │   │       ├── LoginScreen.kt
│   │   │       ├── HomeScreen.kt
│   │   │       ├── ProfileScreen.kt
│   │   │       └── SettingsScreen.kt
│   │   ├── data/                 # Ready for repository pattern
│   │   └── domain/               # Ready for use cases
│   ├── res/
│   │   └── values/
│   │       ├── strings.xml
│   │       ├── colors.xml
│   │       └── themes.xml
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## 🔧 Setup Instructions

1. **Open in Android Studio** (Ladybug or newer recommended)
2. **Sync Gradle** - Let it download all dependencies
3. **Run on Emulator/Device** - API 24+ (Android 7.0+)

### System Requirements
- Android Studio Ladybug (2024.2) or newer
- JDK 17+
- Android SDK Platform 35
- Minimum SDK: API 24

## ✨ Key Features

### Modern Compose Patterns
- `rememberInfiniteTransition` for animations
- `HorizontalPager` for onboarding
- `Scaffold` with custom TopAppBar
- Sealed class navigation routes
- Type-safe navigation arguments

### Material 3 Implementation
- Dynamic color schemes (light/dark)
- Custom shape system
- Proper elevation and surfaces
- Semantic color usage

### Edge-to-Edge Support
- Transparent status bar
- Transparent navigation bar
- Proper inset handling
- WindowCompat integration

## 🎨 Design Highlights

- **Spring Animations** - Bouncy splash screen effect
- **Smooth Transitions** - Pager-based onboarding
- **Consistent Spacing** - 8dp grid system
- **Responsive Layouts** - Adaptive to screen sizes
- **Dark Mode Ready** - Full dark theme support

## 📦 Dependencies

All dependencies are managed via Version Catalog (`gradle/libs.versions.toml`):

- `androidx.compose.bom` 2024.11.00
- `androidx.navigation.compose` 2.8.4
- `androidx.material3` 1.7.5
- `androidx.lifecycle` 2.8.7
- `kotlin` 2.0.21

## 🔄 Next Steps

To extend this project:

1. Add ViewModel layer with `hilt` or manual DI
2. Implement data layer with Room/Repository
3. Add network layer with Retrofit/Ktor
4. Include testing with JUnit and Compose Testing
5. Add CI/CD pipeline

## 📄 License

This project is for educational purposes demonstrating modern Android development patterns.
