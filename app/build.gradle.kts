var accompanist_version = "0.28.0"
var material3_version = "1.1.1"
var nav_version = "2.5.3"
var room_version = "2.5.2"
var hilt_version = "2.44"
var retrofit_version = "2.9.0"
var okhttp3_version = "4.10.0"
var gson_version = "2.10"
var jsoup_version = "1.15.3"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.zabackaryc.xkcdviewer"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.zabackaryc.xkcdviewer"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")

    // BOM
    var composeBom = platform("androidx.compose:compose-bom:2023.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Compose
    // The BOM isn"t used for material3 deps b/c it"s not up-to-date
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:$material3_version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3_version")
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-util")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Dagger/Hilt
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Room
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3_version")
    implementation("com.squareup.okio:okio:3.3.0")
    implementation("com.google.code.gson:gson:$gson_version")
    implementation("org.jsoup:jsoup:$jsoup_version")
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Accompanist
    implementation("com.google.accompanist:accompanist-pager:$accompanist_version")
    implementation("com.google.accompanist:accompanist-webview:$accompanist_version")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist_version")

    // Etc
    implementation("androidx.browser:browser:1.5.0")
    implementation("net.engawapg.lib:zoomable:1.4.3")
}

kapt {
    correctErrorTypes = true
}
