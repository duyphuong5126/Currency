## Table of content
* Done items
* Architecture & Techniques
* Main libraries
* Setup project
* Requisites

## Done items
Feature
* Search for a currency by typing on the currency edit text.
* Select a currency by tapping an item of the dropdown list on the currency edit text.
* Enter the amount of money.
* Converse the inputted amount from the selected currency to all available currencies except that currency.
* Display the conversions on a vertical list.
* Offline execution:
    * The app needs an internet connection at least once to get the currency list. After that, the currency list can be used offline.
    * The conversion feature needs connection at least once to perform. After that, you can use it offline.

## Architecture & Techniques
* Architecture: Clean Architecture
* Main principles: SOLID principles
* Pattern: MVVM, Repository
* Important techniques and libraries: 
    * Kotlin Coroutines
    * Dependency Injection (with Dagger 2)
    * Networking with Retrofit
    * Caching with OkHttp's cache and Room DB

## Main libraries
* Kotlin coroutines android
* Android View Model
* Room Database
* Retrofit
* Dagger 2
* OkHttp
* Paging 3
* Mockito

## Setup project
To run the app.
```
Clone or download the repository from Github.
Open the project from the Currency folder with Android Studio.
Sync project with Gradle files.
Clean and rebuild project.
Run the app on your device or emulator.
```

To run the unit tests.
```
Sync project with Gradle files.
Clean and rebuild project.
Right click on test package (com.phuongduy.currency(test)).
Choose "Run 'Tests in 'currency''" to run all test cases.
Choose "Run 'Tests in 'currency'' with coverage" to run all test cases and see coverage report.
```

## Requisites
* This app requires Android 7.0 (Api 24) or newer
* You must turn on your internet connection at least once to load data from the server
