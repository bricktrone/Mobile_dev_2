plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.khrechko.recyclerviewapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.mirea.khrechko.recyclerviewapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Align Kotlin stdlib and legacy jdk7/jdk8 artifacts to prevent Duplicate class errors.
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")
}
