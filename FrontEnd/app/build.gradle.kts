plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.xlms.librarymanagement"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xlms.librarymanagement"
        minSdk = 23
        targetSdk = 34

        versionCode =
            project.findProperty("VERSION_CODE")?.toString()?.toInt() ?: 1

        versionName =
            project.findProperty("VERSION_NAME")?.toString() ?: "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        val rawBaseUrl =
            project.findProperty("BASE_URL")?.toString()
                ?: "http://localhost:3000"

        var baseUrl = rawBaseUrl.replace("\"", "")

        if (baseUrl.startsWith("BASE_URL=")) {
            baseUrl = baseUrl.substring("BASE_URL=".length)
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }

        buildConfigField(
            "String",
            "BASE_URL",
            "\"$baseUrl\""
        )
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("debug") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {

        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.material:material:1.11.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.activity:activity:1.8.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.google.android.gms:play-services-auth:20.7.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(
        "androidx.test.ext:junit:1.1.5"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.5.1"
    )
}