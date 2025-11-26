## Base Project (Clean Architecture Android Template)

This repository is a **modular Android base project** that demonstrates how to structure small to large apps using **Clean Architecture**, **Jetpack Navigation**, and **Koin** for dependency injection.

It is intended to be a starting point for new apps: you can clone it, change the package name, and immediately start building features on top of a ready‑made architecture.

### Modules & Architecture

- **`app`**: Application entry point (`App`), Koin startup (`KoinModules`), and Gradle wiring to other modules.
- **`core`**: Shared UI and utility layer  
  - Base classes like `BaseActivity`, `BaseFragment`  
  - Common DI (`coreModule`, `dispatchersModule`)  
  - Extensions (navigation helpers, toasts, image loading, etc.)  
  - Firebase Analytics / Crashlytics integration
- **`domain`**: Business logic layer  
  - Use cases, domain models (e.g. `ImageEntity`)  
  - Koin `domainModule`
- **`data`**: Data layer  
  - Data sources & repositories (`dataSourceModules`, `repositoryModules`)  
  - Depends on `core` + `domain`
- **`presentation`**: Feature UI layer (MVI style)  
  - Features such as **Entrance**, **Language**, **Dashboard**, **Media (Images / Videos / Audios)**, **History**, **Premium**, **Settings**, **In‑App Language**, etc.  
  - Navigation graphs (`nav_graph.xml`, `nav_graph_media.xml`, `nav_graph_dashboard.xml`)  
  - ViewModels + `State` / `Intent` / `Effect` patterns (e.g. `LanguageViewModel`, `MediaImagesViewModel`)  
  - Adapters using `DiffUtil` (e.g. `MediaImagesAdapter`, `HistoryAdapter`, `LanguageAdapter`)

The module graph is:

- `app` → `core`, `data`, `domain`, `presentation`
- `presentation` → `core`, `domain`, `data`
- `data` → `core`, `domain`
- `domain` → `core`

### Key Implemented Topics

- **Jetpack Navigation**
  - Single‑Activity setup via `MainActivity` with `FragmentContainerView` and `nav_graph`
  - Nested graphs for dashboard and media (`nav_graph_dashboard.xml`, `nav_graph_media.xml`)
  - Global navigation actions from anywhere (e.g. `action_global_premiumFragment`)
  - `DashboardFragment` hosts its own `NavHostFragment` with bottom navigation

- **ViewModel + MVI pattern**
  - ViewModels use **state** + **intent** + **effect** (e.g. `LanguageState`, `LanguageIntent`, `LanguageEffect`)
  - UI collects flows using `collectWhenStarted` extension
  - Clear separation of UI state rendering vs one‑off side effects

- **Coroutines**
  - Kotlin coroutines with dispatcher injection via `dispatchersModule`
  - Structured concurrency inside ViewModels and use cases

- **Dependency Injection (Koin)**
  - `App` starts Koin with `lazyModules(KoinModules().getKoinModules())`
  - DI modules per feature in `presentation`, plus `core`, `data`, and `domain` modules
  - ViewModels obtained via `by viewModel()` from Koin

- **Firebase**
  - Firebase Analytics & Crashlytics added in `core` module dependencies

- **App Language & In‑App Language**
  - Startup language selection flow: `EntranceFragment` → `LanguageFragment` → `DashboardFragment`
  - Runtime language change: `InAppLanguageFragment` (with `InAppLanguageAdapter`)
  - Uses `AppLocalesMetadataHolderService` in `AndroidManifest` for locale persistence on older Android versions

- **DiffUtil & ListAdapter**
  - Reusable list patterns with `ListAdapter` + `DiffUtil` for efficient updates  
    e.g. `MediaImagesAdapter`, `MediaVideosAdapter`, `MediaAudiosAdapter`, `HistoryAdapter`, `LanguageAdapter`, `InAppLanguageAdapter`

- **Back Handling & Navigation Helpers**
  - Centralized back handling in `MainActivity` using `onBackPressedDispatcher` extension  
    - Custom behavior for `entranceFragment` and `languageFragment`
  - Shared `navigateTo` helpers in `BaseFragment` for navigating by id or by directions

- **Permissions & Media Access**
  - Manifest already includes all modern storage/media permissions (Android 14+ and legacy)  
  - Media features (images / videos / audio) show how to query and display device media via the clean architecture layers

### Requirements

- **Android Studio** Giraffe or newer (for Kotlin + Gradle KTS)
- **JDK 17**
- **Android SDK** 24+
- Firebase configuration via `google-services.json` (already present in `app`)

### Getting Started

- **1. Clone & open**
  - Clone this repo.
  - Open the root folder in Android Studio.

- **2. Configure Firebase (optional but recommended)**
  - Replace the existing `google-services.json` inside `app` with your project’s file.
  - Make sure Firebase Analytics / Crashlytics are enabled in your Firebase project.

- **3. Run the app**
  - Select the `app` configuration.
  - Run on a device/emulator (min SDK 24, target/compile SDK 36).

- **4. Start customizing**
  - Change `applicationId` & `namespace` in `app/build.gradle.kts`.
  - Update package name under `com.hypersoft.baseproject` to your own.
  - Modify or remove example features under `presentation` as needed.

### How to Reuse This Base in a New Project

- **Rename package & app id**
  - Use Android Studio’s refactor to rename `com.hypersoft.baseproject` to your own package.
  - Update `applicationId` in `app/build.gradle.kts`.

- **Keep the modules**
  - Keep the `core`, `domain`, `data`, and `presentation` modules as a template.
  - Remove demo features you don’t need (History, Premium, Media, etc.).

- **Add new features**
  - Create a new feature package in `presentation` with:
    - `state`, `intent`, `effect`, `viewModel`, `ui` packages
    - Koin module for the feature
  - Add new destinations to `nav_graph.xml` or child graphs.

### License

This project is licensed under the **Apache License 2.0**.

Copyright 2023 Hypersoft Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
