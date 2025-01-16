plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.projekconsultant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projekconsultant"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //Multidex Pie Chart
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Dependency Firebase Dan Bawaan Android Studio
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    //implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Firesotre Terbaru
    implementation("com.google.firebase:firebase-firestore:24.3.0")
    //Google
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Library Flebox Untuk Tag
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    //Card View XML
    implementation("androidx.cardview:cardview:1.0.0")
    // implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")
    //Pie Chart Library Github Android Link : https://github.com/PhilJay/MPAndroidChart?tab=readme-ov-file
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //PDf
    implementation("com.itextpdf:itext7-core:7.2.3")

    //implementation("com.github.AnyChart:AnyChart-Android:1.1.5")
    //Glide Picture
    implementation("com.github.bumptech.glide:glide:4.12.0")
    testImplementation("junit:junit:4.13.2")
}




