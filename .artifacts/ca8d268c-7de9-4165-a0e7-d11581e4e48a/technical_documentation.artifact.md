# Technical Documentation — The Code Cup

This document details the architectural approaches, data flow, and technical design decisions used to build "The Code Cup" Android application.

---

## 🏗️ Architectural Overview

The app follows the **Modern Android Development (MAD)** recommendations, utilizing a **Single-Activity Architecture** combined with **MVVM (Model-View-ViewModel)** and **Clean Architecture** principles.

### **Layers of the App**
1.  **UI Layer (Fragments & ViewModels)**: Handles user interaction and state rendering.
2.  **Domain Layer (Models)**: Defines the core data structures (`Coffee`, `Order`, `Customization`).
3.  **Data Layer (Repositories & Data Sources)**: Manages data retrieval from Room (SQLite) and SharedPreferences.
4.  **DI Layer (Manual Injection)**: Provides dependencies to the entire app without the overhead of heavy frameworks.

---

## 🛠️ Frameworks & Dependencies

The project is built with **Kotlin** and targets **SDK 37**. Key libraries include:

-   **Jetpack Navigation**: Orchestrates screen transitions via an XML Nav Graph.
-   **Room Persistence**: A type-safe abstraction over SQLite for relational data.
-   **ViewBinding**: Generates binding classes for XML layouts, ensuring null-safe view access.
-   **Kotlin Coroutines & Flow**: Powers the reactive data streams and background threading.
-   **Material Components**: Provides modern UI elements like `MaterialCardView` and `Slider`.
-   **KSP (Kotlin Symbol Processing)**: Used for high-performance Room code generation.
-   **Parcelize**: Enables efficient passing of complex objects (`Customization`) between Fragments.

---

## 🔄 Data Flow & Rendering

### **1. The "Source of Truth" Flow**
Data flows in a reactive loop from the database up to the screen:
1.  **Database**: Room DAOs return a `Flow<List<Entity>>`.
2.  **Repository**: Converts database entities into clean domain models and exposes them as `Flow`.
3.  **ViewModel**: Transforms these flows into `StateFlow` using `stateIn`. It handles filtering (e.g., Menu search) and logic.
4.  **Fragment**: Uses `lifecycleScope` to "collect" the state. When the database changes, the UI updates automatically.

### **2. Interaction Flow**
1.  User clicks a button (e.g., "Add to Cart").
2.  Fragment calls a method on the **ViewModel**.
3.  ViewModel launches a **Coroutine** and calls the **Repository**.
4.  Repository updates the **DAO** (Database).
5.  Since the UI is observing the Database Flow, it refreshes instantly with the new data.

---

## 💾 Persistence Strategy

The app utilizes two distinct storage methods based on data complexity:

| Data Type | Storage Method | Rationale |
| :--- | :--- | :--- |
| **Relational** (Menu, Cart, Orders, Favorites) | **Room (SQLite)** | Handles complex relationships (e.g., an Order contains multiple OrderItems). Supports powerful querying and search. |
| **Singleton / Key-Value** (Profile, Loyalty Stats) | **SharedPreferences** | Ideal for single-row data like the user's name or current stamp count. Lower overhead than a database table. |

### **Database Seeding & Initialization**
The app uses a `RoomDatabase.Callback` to populate the `coffees` and `redeemable_items` tables.
- **First Launch Detection**: Seeding is triggered by the `onCreate` method of the callback. This is a built-in Room feature that fires **exactly once** in the lifetime of the database file—specifically when the SQLite database is created for the first time.
- **Idempotency**: The seeding logic is **implicitly idempotent** because it only runs when the tables are guaranteed to be empty (during creation). However, the underlying DAO methods use a standard `@Insert` strategy; if the seeding logic were manually triggered again on an existing database, it would result in a primary key conflict.
- **Resetting Data**: Currently, the only way to re-trigger the seeding (e.g., to load updated prices or new drinks) is to **clear the app's data** or uninstall the app. This deletes the physical `.db` file, forcing Room to recreate it and re-run the `onCreate` callback.

### **Special Pattern: Historical Snapshots**
To ensure order history remains accurate if a user moves, the app uses a **Snapshot Pattern**. When an order is placed, the user's *current* address is copied from the Profile Preferences and hard-saved into the `OrderEntity` table.

---

## 🚦 Navigation & State

### **Manual Dependency Injection**
To keep the app transparent and easy to explain, we used an `AppContainer` pattern:
- **`CodeCupApplication`**: Initializes the `AppDataContainer` on startup.
- **`AppContainer`**: A single object that holds "lazy" instances of all repositories and database sources.
- **`ViewModelFactory`**: A centralized factory that pulls dependencies from the container and injects them into the ViewModels.

### **Smart Navigation management**
`MainActivity` acts as the global controller for:
- **Navbar Visibility**: Monitors the `NavController` and hides the bottom bar on stack-only screens.
- **Notification Channels**: Sets up the "Rewards" channel for rank-up alerts.
- **Custom Header Logic**: Centralizes time-based greetings and profile navigation.

---

## ⏳ State & Lifecycle Management

The application employs a reactive, lifecycle-aware approach to state management, ensuring a seamless user experience during standard navigation and configuration changes.

### **1. Configuration Change Survival (Rotation)**
All ViewModels are scoped to their respective Fragments. Because ViewModels are retained by the `ViewModelStore` during configuration changes, all "in-flight" state—data that has been modified but not yet persisted—survives screen rotations automatically.
- **Example**: If a user is on the **Details screen**, they can adjust the sweetness and heat levels, rotate the phone, and their selections will remain exactly as they left them. This is handled by the `customization` `StateFlow` inside the `DetailsViewModel`.

### **2. Process Death Behavior**
While ViewModels survive rotation, they do not survive **Process Death** (when the OS kills the app to reclaim memory).
- **In-flight State Loss**: Currently, the app does *not* implement `SavedStateHandle`. This means half-filled forms (like an unsaved Profile edit or an un-submitted drink customization) will reset to their default values if the process is killed.
- **Persistence Safety**: Any data that has been explicitly "saved" or "added" (e.g., items in the **Cart**, completed **Orders**, or a saved **Profile**) is stored in Room or SharedPreferences and is therefore **immune to process death**.

### **3. Reactive Lifecycle Awareness**
A deliberate design choice was made to avoid manual overrides of `onPause`, `onResume`, or `onStop` for business logic. Instead, the app relies on **Reactive Streams**:
- **`repeatOnLifecycle(Lifecycle.State.STARTED)`**: Fragments use this utility to observe `StateFlow`s. This ensures that the UI only consumes data when it is in the foreground, automatically pausing observers when the app is in the background to save battery and resources.
- **Deliberate Design**: This approach is cleaner and more robust than manual lifecycle callbacks, as it prevents "leaks" or crashes caused by trying to update a UI that is no longer visible.

### **4. Component-Level State**
- **Home Hero Card**: The "Special" drink of the day is selected once in the `HomeViewModel`'s `init` block. This ensures the hero card doesn't "flicker" or change to a different drink when the user rotates the screen.
- **Profile Edit Mode**: The toggle between "View" and "Edit" mode is handled as a visual state within the `ProfileFragment`. Since the `ViewModel` pushes the latest saved data to the `EditTexts` upon collection, the user's saved info is always the baseline.

---

## 🧠 Key Technical Design Decisions

1.  **Smart Recipe Recognition**: Instead of favoriting a drink ID, we compare the entire `Customization` object. This required making the model `Parcelable` to move it through the Nav Graph.
2.  **Reactive Favorites**: The heart icon is a "derived state." It doesn't store a boolean; instead, it's a `Flow` that calculates `current_screen_settings IN saved_recipes_list` in real-time.
3.  **Cart Merging**: The `CartRepository` performs a "deep check" on every addition. If it finds an existing item with the exact same customizations, it runs an `UPDATE` query on the quantity instead of an `INSERT`, preventing cart clutter.
4.  **Dynamic UI Toggling**: Used `androidx.constraintlayout.widget.Group` in XML to atomically switch between "Empty States" and "Data Content," reducing manual visibility code in Fragments.
