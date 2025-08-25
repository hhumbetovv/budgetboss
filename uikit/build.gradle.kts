plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.uikit"
}

dependencies {
    //! Modules
    implementation(projects.common)
    implementation(projects.core)
    implementation(projects.domain)

    //? Bundles
    implementation(libs.bundles.android.core)
}