plugins { alias(libs.plugins.android.application) }
android {
    namespace = "ru.mirea.khrechko.navigationdrawerapp"
    compileSdk = 36
    defaultConfig { applicationId = "ru.mirea.khrechko.navigationdrawerapp"; minSdk = 26; targetSdk = 36; versionCode = 1; versionName = "1.0" }
    buildFeatures { viewBinding = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
}
