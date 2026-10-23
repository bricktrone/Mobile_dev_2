plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.khrechko.movieproject"
    compileSdk = 36
    defaultConfig {
        applicationId = "ru.mirea.khrechko.movieproject"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Align Kotlin stdlib and legacy jdk7/jdk8 artifacts to prevent Duplicate class errors.
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
