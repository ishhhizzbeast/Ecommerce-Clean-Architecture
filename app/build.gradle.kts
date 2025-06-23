import org.gradle.kotlin.dsl.annotationProcessor
import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id ("com.google.devtools.ksp")

    }

android {
    namespace = "com.example.rushbuy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rushbuy"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    //implementation ("androidx.core:core-splashscreen:1.0.1")
    //downloadable font family
    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.1")

    //room db
    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")

    ksp ("androidx.room:room-compiler:$room_version")


    //for koin
    implementation("io.insert-koin:koin-android:4.0.3")
    implementation("io.insert-koin:koin-core:4.0.3")
    implementation("io.insert-koin:koin-androidx-compose:4.0.3")
    implementation("io.insert-koin:koin-androidx-compose-navigation:4.0.3")

    //extended-material-icon
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    //kotlin coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")


    //Firebase
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    //kotlin serilization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    // Or, if using Kotlin Serialization
    // Gson Converter for Retrofit (This is where @SerializedName comes from)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Google Sign In
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    // Add these new Credential Manager dependencies:
    implementation ("androidx.credentials:credentials:1.2.2")
    implementation ("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0") // Or your current Coil version

    // Add ViewModel and LiveData/Flow lifecycles
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("androidx.compose.material3:material3")




}