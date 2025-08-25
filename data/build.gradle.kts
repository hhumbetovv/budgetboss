plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.data"
}

dependencies {
    //! Modules
    implementation(projects.common)
    implementation(projects.domain)

    //? Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.compiler)

    //? Network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //? Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //? Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics)
}