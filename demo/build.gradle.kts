import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.glimpse)
}

// Example of using the plugin extension.
// The values given below are the defaults.
// Be sure to rebuild after any changes.
glimpse {
    remoteAdapter = true
    scrollableLazyColumn = true
    scrollableLazyVerticalGrid = true
    suppressPluginLogs = false
}

android {
    namespace = "dev.gonodono.glimpse.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.gonodono.glimpse.demo"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
}

dependencies {

    implementation(projects.library)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.remoteviews)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
}