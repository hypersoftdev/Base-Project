plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.hypersoft.baseproject.core"

    compileSdk = 36

    defaultConfig {
        minSdk = 24

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Android Core
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Splash Screen Api
    implementation(libs.androidx.core.splashscreen)

    // Firebase
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime)

    // Navigational Components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Dependency Injection -> Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core.coroutines)

    // Glide
    implementation(libs.glide)

    // Palette
    implementation(libs.androidx.palette)
}