## Architecture

- **MVI + Clean Architecture**
- **Multi‑module (by layers)**

## Topics Covered

- **Kotlin Coroutines**
- **Kotlin Flows**
- **ViewModel / MVI State–Intent–Effect**
- **Jetpack Navigation**
- **Koin Dependency Injection**
- **DiffUtil / ListAdapter**
- **Multi‑module setup**
- **Firebase (Analytics, Crashlytics)**
- **MediaStore**
- **SharedPreferences**

## Modules

- **`app`**
- **`core`**
- **`domain`**
- **`data`**
- **`presentation`**

## Module Dependency Graph

- `app` → `core`, `domain`, `data`, `presentation`
- `presentation` → `core`, `domain`, `data`
- `data` → `core`, `domain`
- `domain` → `core`
