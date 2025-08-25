plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.home"
}

dependencies {
    //! Modules
    implementation(projects.common)
    implementation(projects.uikit)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(projects.data)

    implementation(projects.feature.record.details)

    //? Bundles
    implementation(libs.bundles.android.core)

    //? Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //? Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
}