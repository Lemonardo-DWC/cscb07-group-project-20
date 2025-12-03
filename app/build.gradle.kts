plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartair"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.smartair"
        minSdk = 35
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //add dependencies for Mockito for testing
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("junit:junit:4.13.2")

    ///////////////   Firebase Dependencies   ///////////////

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies

    // Analytics
    implementation(libs.firebase.analytics)

    // Authentication
    implementation(libs.firebase.authentication)

    // Realtime Database
    implementation(libs.firebase.realtime.database)

    // Cloud Functions
    implementation(libs.firebase.functions)

    // Cloud Messaging
    implementation(libs.firebase.messaging)

    // Youtube Player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:13.0.0")

    //send invitation link
    implementation("com.google.firebase:firebase-functions:20.3.1")

    implementation("com.android.volley:volley:1.2.1")

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // iText PDF
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    // chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


    // multi select calendar
    implementation("com.applandeo:material-calendar-view:1.9.0")

}