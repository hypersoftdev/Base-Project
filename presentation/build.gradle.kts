plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.navigation.safe.args)
}

android {
    namespace = "com.hypersoft.baseproject.presentation"

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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Modules
    implementation(project(":core"))

    // Domain Modules
    implementation(project(":domain"))
    implementation(project(":data"))

    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Fragment Ktx
    implementation(libs.androidx.fragment.ktx)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Navigational Components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Dependency Injection -> Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core.coroutines)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lottie Animation
    implementation(libs.lottie)

    // Swipe Refresh Layout
    implementation(libs.androidx.swiperefreshlayout)

    // Media3 ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Glide
    implementation(libs.glide)
}

