plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.navigation.safe.args)
}

android {
    namespace = "com.hypersoft.baseproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hypersoft.baseproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 21
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Use the "release" signing configuration for the release build
    /*signingConfigs {
        create("release") {
            storeFile = file("D:\\User\\keyStore\\BaseProject.jks")
            storePassword = "BaseProject"
            keyAlias = "BaseProject"
            keyPassword = "BaseProject"
        }
    }*/

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            // Use the "release" signing configuration for the release build
            // signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
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
        buildConfig = true
    }

    // Disable language-specific APK splits, include all languages in a single APK
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    // Modules
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Splash Screen Api
    implementation(libs.androidx.core.splashscreen)

    // Navigational Components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Dependency Injection -> Koin
    implementation(libs.koin.android)
    implementation(libs.koin.core.coroutines)

    // Swipe Refresh Layout
    implementation(libs.androidx.swiperefreshlayout)

    // Lottie Animations
    implementation(libs.lottie)
}