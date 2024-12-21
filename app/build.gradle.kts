import java.io.ByteArrayOutputStream

val accompanistVersion = "0.28.0"
val material3Version = "1.1.1"
val navVersion = "2.8.5"
val roomVersion = "2.6.1"
val hiltVersion = "2.49"
val retrofitVersion = "2.9.0"
val okhttp3Version = "4.10.0"
val gsonVersion = "2.10.1"
val jsoupVersion = "1.15.3"

val gitDescribeTags: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git", "describe", "--tags", "--abbrev=0")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

val gitDescribeLong: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git", "describe", "--tags", "--long")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "com.zabackaryc.xkcdviewer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zabackaryc.xkcdviewer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "GIT_TAG", "\"$gitDescribeTags\"")
        buildConfigField("String", "GIT_VERSION_LONG", "\"$gitDescribeLong\"")

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
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("androidx.core:core-ktx:1.15.0")

    // BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Compose
    // The BOM isn"t used for material3 deps b/c it"s not up-to-date
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Dagger/Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3Version")
    implementation("com.squareup.okio:okio:3.3.0")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Accompanist
    implementation("com.google.accompanist:accompanist-webview:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    // Etc
    implementation("androidx.browser:browser:1.8.0")
    implementation("net.engawapg.lib:zoomable:1.4.3")
    implementation("com.github.alorma:compose-settings-ui-m3:1.0.2")
    implementation("com.github.alorma:compose-settings-storage-preferences:1.0.2")
    implementation("com.mikepenz:aboutlibraries-core:10.9.2")
}
