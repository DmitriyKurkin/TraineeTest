plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.traineetest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.traineetest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {


        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("androidx.activity:activity-ktx:1.9.2")

        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

        implementation("androidx.recyclerview:recyclerview:1.3.2")

        implementation("com.github.bumptech.glide:glide:4.16.0")

        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

}