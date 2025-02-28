# SA

An Android application that displays Intuit's open-source repositories from GitHub, with features for viewing repository details, searching, and offline support.

## Project Structure

This project follows modern Android development practices:

- **Architecture**: MVVM with Clean Architecture principles
- **UI**: Jetpack Compose for a modern, declarative UI
- **Dependency Injection**: Hilt
- **Local Storage**: Room Database
- **Networking**: Retrofit with OkHttp
- **Concurrency**: Kotlin Coroutines and Flow
- **Testing**: JUnit, Mockito, and Compose UI testing

## Build System

The project uses Gradle with Kotlin DSL (KTS) for improved type safety and IDE support. Key features include:

- **Version Catalog**: Centralized dependency management in `gradle/libs.versions.toml`
- **Kotlin DSL**: Type-safe build scripts with better IDE support
- **Dependency Resolution Management**: Centralized repository and dependency configuration

### Version Catalog

The project uses Gradle's Version Catalog feature with a TOML file (`gradle/libs.versions.toml`) to manage dependencies and versions. This approach provides:

- Centralized version management
- Type-safe dependency accessors
- Better IDE support for dependency management
- Easier dependency updates

Example usage in build scripts:
```kotlin
dependencies {
    implementation(libs.core.ktx)
    implementation(libs.compose.ui)
    // ...
}
```

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the application

## Features

- View a list of Intuit's open-source repositories
- Search repositories by name
- View detailed information about each repository
- Offline support with local caching
- Error handling and retry mechanisms

## Requirements

- Android Studio Hedgehog or newer
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Kotlin 1.9.10 or newer

## Screen Orientation Handling

The app handles screen orientation changes by:
- Using ViewModels to retain data across configuration changes
- Using Jetpack Compose's state management
- Configuring the main activity to handle orientation changes

## Compliance with Platform Guidelines

- Material Design components and patterns
- Proper handling of system resources
- Accessibility considerations
- Efficient background processing

## License

This project is for demonstration purposes only. 