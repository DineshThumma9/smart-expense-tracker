

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services") // Firebase Plugin
}

android {
    namespace = "com.example.something"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.something"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "META-INF/*.kotlin_module",
                "META-INF/native-image/org.mongodb/bson/native-image.properties",
                "META-INF/native-image/**"
            )
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
        release {
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
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
        jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
    }
}

// Exclude the desugaring library from the androidTest configuration
configurations {
    androidTestImplementation {
        exclude(group = "com.android.tools", module = "desugar_jdk_libs")
    }
}

dependencies {
    // ✅ Core AndroidX Dependencies
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.storage)

    // ✅ Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ✅ Material & Fonts
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.text.google.fonts)

    // ✅ Ktor (Use the latest stable 2.x version)
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-okhttp:2.3.6")
    implementation("io.ktor:ktor-client-serialization:2.3.6")
    implementation("io.ktor:ktor-client-logging:2.3.6")

    // ✅ Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // ✅ MongoDB Kotlin Coroutine Driver
    implementation(libs.mongodb.driver.kotlin.coroutine.v520)
    implementation("org.mongodb:mongodb-driver-sync:4.11.1") {
        exclude(group = "org.mongodb", module = "mongodb-driver-core")
        exclude(group = "org.mongodb", module = "bson")
        exclude(group = "org.mongodb", module = "mongodb-driver-legacy")
    }

    // ✅ BSON (Fix BSON Codec Issue)
    implementation("org.mongodb:bson:4.10.1")

    // ✅ Coroutines Reactive (For `asFlow()`)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.1")

    // ✅ Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ✅ Core Library Desugaring for Java 11+
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // 🔥 **Firebase Dependencies**
    implementation(platform("com.google.firebase:firebase-bom:33.9.0")) // Firebase BOM for auto-versioning
    implementation("com.google.firebase:firebase-auth-ktx")           // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore-ktx")      // Firestore Database
    implementation("com.google.firebase:firebase-messaging-ktx")      // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-analytics-ktx")      // Firebase Analytics (optional)
}