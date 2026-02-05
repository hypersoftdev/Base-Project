plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.hypersoft.baseproject.domain"

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
}

dependencies {
    // Core Modules
    implementation(project(":core"))

    // Dependency Injection -> Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core.coroutines)
}