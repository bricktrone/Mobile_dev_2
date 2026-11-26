plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    buildFeatures { viewBinding = true }
    androidResources {
        noCompress += "tflite"
    }
    namespace = "ru.mirea.khrechko.foodscan"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.mirea.khrechko.foodscan"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Google Code Scanner: camera-based barcode UI powered by Google Play services.
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    // TensorFlow Lite Task Vision image classifier.
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.room:room-runtime:2.8.5")
    annotationProcessor("androidx.room:room-compiler:2.8.5")
    implementation(libs.appcompat)
    implementation("androidx.fragment:fragment:1.8.5")
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}