plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp.devtools)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.asp.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutine.android)
    api(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.room.common)
    implementation(libs.hilt.android)
    implementation(libs.moshi)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi.converter)
    implementation(project(":domain"))

    annotationProcessor(libs.room.compiler)

    ksp(libs.room.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.moshi.codegen)

    testImplementation(libs.junit)
    testImplementation(libs.mockk.io)
    testImplementation(libs.room.testing)
    testImplementation(libs.kotlin.coroutine.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}