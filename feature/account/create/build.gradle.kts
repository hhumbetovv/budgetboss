plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.create_account"
}

dependencies {
    //! Modules
    implementation(projects.common)
    implementation(projects.uikit)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(projects.data)

    //? Bundles
    implementation(libs.bundles.android.core)

    implementation(libs.androidx.fragment.ktx)

    //? Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
}