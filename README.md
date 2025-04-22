# Yoga Admin - Android Application

## Project Overview

Yoga Admin is an Android application designed to provide yoga studio administrators with a comprehensive tool for managing their classes, teachers, class types, and scheduled class instances. Built following modern Android development best practices, the application utilizes the MVVM architecture, Room Persistence Library for local data, and integrates with Firebase Realtime Database for cloud synchronization and backup.

## Features

*   **Class Management:** Add, edit, delete, and view details of yoga class definitions (days, time, capacity, duration, price, type, level, room).
*   **Class Type Management:** Add, edit, delete, and view different types of yoga classes (e.g., Hatha, Vinyasa).
*   **Teacher Management:** Add, edit, delete, and view teacher information and their associated class types.
*   **Class Instance Management:**
    *   View scheduled class instances in a date-grouped list.
    *   Generate class instances for a specific class, teacher, and date range.
    *   Search and filter class instances by teacher name, day of the week, and date.
    *   Edit and delete individual class instances.
    *   View detailed information for a class instance and its associated class by clicking on the item.
*   **Data Synchronization (Firebase):**
    *   Manually upload all or individual data types (Classes, Class Types, Teachers, Class Instances) to Firebase Realtime Database.
    *   Automatic background synchronization of local data changes to Firebase when a network connection is available (using WorkManager).
    *   Network connectivity checks before initiating cloud uploads.
*   **User-Friendly Interface:** Implemented using Material Design Components for a modern, intuitive, and consistent user experience.
*   **Offline Capability:** Local data persistence with Room allows the application to function for data management even without an internet connection (cloud sync requires network).

## Architecture

The application is built adhering to the **Model-View-ViewModel (MVVM)** architectural pattern, promoting a clean separation of concerns, testability, and maintainability.

*   **Model:** Manages the application data and business logic. Includes Room entities, DAOs, and Repositories.
*   **View:** Represents the user interface (Activities, Fragments, Adapters). Observes data from the ViewModel.
*   **ViewModel:** Acts as a mediator between the Model and the View. Prepares and holds UI-specific data, handles UI logic and user actions, and exposes data via `LiveData`.

Android Architecture Components such as `LiveData`, `MutableLiveData`, `MediatorLiveData`, and `ViewModel` are extensively used to create a reactive and lifecycle-aware data pipeline.

Background operations, including database interactions and Firebase uploads, are handled off the main thread using `AsyncTask` and `ExecutorService` for responsiveness, with `WorkManager` specifically employed for reliable periodic background synchronization.

## Getting Started

### Prerequisites

*   Android Studio installed.
*   Android SDK installed.
*   A Firebase project set up for your application.

### Setup

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/Danh-coder/YogaAdminApp.git
    ```

2.  **Open in Android Studio:** Open the cloned project in Android Studio.

3.  **Configure Firebase:**
    *   Add your Android application to your Firebase project.
    *   Download the `google-services.json` file from your Firebase project settings.
    *   Place the `google-services.json` file in the `app/` directory of your Android project.
    *   Ensure Firebase Realtime Database is enabled for your project in the Firebase console.
    *   Configure Firebase Realtime Database Security Rules (crucial for securing your data in the cloud - **Note: Security Rules are not included in the provided code and must be configured separately in the Firebase Console**).

4.  **Sync Project with Gradle Files:** Sync the project in Android Studio to download all necessary dependencies.

5.  **Build and Run:** Build and run the application on an Android emulator or physical device.

## Dependencies

Key dependencies used in this project include:

*   [AndroidX Libraries](https://developer.android.com/jetpack/androidx) (Core, AppCompat, ConstraintLayout, RecyclerView, CardView)
*   [Material Design Components](https://material.io/develop/android)
*   [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)
*   [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) (ViewModel, LiveData, Transformations)
*   [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
*   [Firebase Realtime Database SDK](https://firebase.google.com/docs/database/android/start)

Refer to the `app/build.gradle` file for the complete list of dependencies.

## Future Improvements

*   **Implement User Authentication and Authorization:** Secure the application with user login and role-based access control.
*   **Implement Room Database Encryption:** Add encryption for data at rest in the local Room database.
*   **Improve Data Synchronization Conflict Resolution:** Implement more robust strategies for handling data conflicts during synchronization.
*   **Enhance UI/UX:** Further refine the user interface with animations, transitions, and accessibility features.
*   **Optimize for Tablets and Landscape Orientation:** Create adaptive layouts to provide an optimized user experience on larger screens and in landscape mode.
*   **Add More Granular Upload Options:** Allow users to select specific records or date ranges for manual upload.
*   **Implement Data Download from Firebase:** Add functionality to download data from Firebase to the local database (e.g., for new devices or data recovery).
*   **Implement Class Instance Add/Edit UI (Dedicated):** Create dedicated UI for adding and editing individual class instances.
*   **Refine Filter/Search Logic:** Further optimize filtering and search performance for very large datasets.
*   **Add Comprehensive Input Validation:** Implement more thorough validation for user input in all forms.
*   **Implement Error Reporting and Monitoring:** Integrate a crash reporting service (e.g., Firebase Crashlytics) and monitoring tools for live use.
*   **Develop Customer-Facing Application:** Build the envisioned customer application, integrating with the same Firebase backend.
