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

## ⭐ Star This Repo

If you find this project helpful, please consider giving it a star! It helps others discover this base project.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

# Acknowledgements

We are deeply grateful for [Sohaib Ahmed](https://github.com/epegasus) involvement and his belief in the importance of this work. His contributions have made a significant impact, and we are honored to have had the opportunity to collaborate with him.


## License

Copyright 2025 Hypersoft Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.