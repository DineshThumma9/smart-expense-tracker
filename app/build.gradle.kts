plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
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
        buildConfigField("String", "MONGODB_URI", "\"${project.findProperty("MONGODB_URI") ?: ""}\"")
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "META-INF/*.kotlin_module",
                "META-INF/native-image/org.mongodb/bson/native-image.properties",
                "META-INF/native-image/**",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/services/javax.annotation.processing.Processor"
            )
        )
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
        buildConfig = true
    }
}

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

    // ✅ MongoDB Driver (Excluding Unnecessary Modules)
    implementation(libs.mongodb.driver.kotlin.coroutine.v530)


    // ✅ BSON Library
    implementation(libs.bson.v4101)

    // ✅ Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.logging)

    // ✅ Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ✅ Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ✅ Core Library Desugaring for Java 11+
    coreLibraryDesugaring(libs.desugar.jdk.libs)


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.1")


    // ✅ Material & Fonts
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.text.google.fonts)

    // 🔥 **Firebase Dependencies**
    implementation(platform("com.google.firebase:firebase-bom:33.10.0")) // Firebase BOM for auto-versioning
    implementation("com.google.firebase:firebase-auth-ktx")           // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore-ktx")      // Firestore Database
    implementation("com.google.firebase:firebase-messaging-ktx")      // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-analytics-ktx")      // Firebase Analytics (optional)
}