plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.mestero"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mestero"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "environment"
    
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ""
            versionCode = 2
            versionName = "1.0-dev"
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
            buildConfigField("String", "COLLECTION_PREFIX", "\"\"")
        }
        
        create("prod") {
            dimension = "environment"
            applicationIdSuffix = ".prod"
            versionCode = 1
            versionName = "1.0-prod"
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
            buildConfigField("String", "COLLECTION_PREFIX", "\"prod_\"")
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Async coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Firebase
//    implementation("com.google.firebase:firebase-auth:23.2.0")
//    implementation("androidx.credentials:credentials:1.5.0")
//    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
//    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Add the dependency for the Firebase Authentication library
    // Firebase Analytics & Performance
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    kapt("com.google.dagger:hilt-compiler:2.51.1")


    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    //Data binding
//    kapt 'com.android.databinding:compiler:4.1.3'


}