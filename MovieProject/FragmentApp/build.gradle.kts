plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.khrechko.fragmentapp"
    compileSdk = 36
    defaultConfig {
        applicationId = "ru.mirea.khrechko.fragmentapp"
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
}
