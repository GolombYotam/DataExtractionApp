# DataExtractionApp

DataExtractionApp is an Android application that extracts and displays device data, media metadata, and contacts information. The application uses Jetpack Compose for the UI and Kotlin coroutines for asynchronous tasks.

## Features

- Display device information such as model, OS version, and manufacturer.
- Display media metadata for images and videos.
- Display contacts information from the device.
- Perform analysis on contacts and media data.

## Requirements

- Android Studio Flamingo or higher
- Kotlin 1.5.31 or higher
- Gradle 7.0 or higher
- Android SDK 21 or higher

## Setup

1. **Clone the repository**

    ```sh
    git clone https://github.com/yourusername/DataExtractionApp.git
    cd DataExtractionApp
    ```

2. **Open the project in Android Studio**

    - Open Android Studio.
    - Select `Open an existing Android Studio project`.
    - Navigate to the cloned repository directory and click `OK`.

3. **Sync the project with Gradle files**

    - Once the project is open, click `File > Sync Project with Gradle Files`.

4. **Build the project**

    - Click `Build > Rebuild Project`.

## Running the App

1. **Connect an Android device or start an emulator.**

2. **Run the app**

    - Click `Run > Run 'app'`.

## Testing

### Unit Tests

Unit tests are located in the `src/test/java/com/example/sdk` directory. These tests cover the logic in the `DataExtractor` class.

1. **Run unit tests**

    - Open the `DataExtractorTest` class.
    - Right-click and select `Run 'DataExtractorTest'`.

### UI Tests

UI tests are located in the `src/androidTest/java/com/example/dataextractionapp` directory. These tests cover the UI components using Jetpack Compose testing.

1. **Run UI tests**

    - Open the `ComposablesTest` class.
    - Right-click and select `Run 'ComposablesTest'`.

## Project Structure

DataExtractionApp/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/example/dataextractionapp/
│ │ │ │ ├── MainActivity.kt
│ │ │ │ ├── ComposableFactory.kt
│ │ │ │ ├── ui/theme/
│ │ │ │ │ ├── Color.kt
│ │ │ │ │ ├── Theme.kt
│ │ │ │ │ ├── Type.kt
│ │ │ ├── res/
│ │ │ │ ├── layout/
│ │ │ │ ├── values/
│ ├── build.gradle
│ ├── AndroidManifest.xml
├── sdk/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/example/sdk/
│ │ │ │ ├── DataExtractor.kt
│ │ ├── test/
│ │ │ ├── java/com/example/sdk/
│ │ │ │ ├── DataExtractorTest.kt
├── README.md


## Dependencies

- Jetpack Compose
- Accompanist Permissions
- Kotlin Coroutines
- MockK (for unit testing)
- JUnit (for unit testing)
- AndroidX Test (for UI testing)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

