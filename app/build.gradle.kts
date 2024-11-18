plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Certifique-se de que o plugin esteja aqui
}

android {
    namespace = "com.example.vcardapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vcardapp"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
            // Definição do campo BuildConfig para release
            buildConfigField("String", "FCM_SERVER_KEY", "\"${System.getenv("FCM_SERVER_KEY")}\"")
        }

        getByName("debug") {
            isDebuggable = true
            // Definição do campo BuildConfig para debug
            buildConfigField("String", "FCM_SERVER_KEY", "\"${System.getenv("FCM_SERVER_KEY")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
