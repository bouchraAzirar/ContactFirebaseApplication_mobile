plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.contact_firebase"  // ajoute namespace (recommandé)
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.contact_firebase"
        minSdk = 26          // <-- corrigé pour éviter l'erreur
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    // Android UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase (BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // Google Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
