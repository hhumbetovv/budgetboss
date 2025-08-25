plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.core"
}

dependencies {
    //! MODULES
    implementation(projects.common)

    //? Bundles
    implementation(libs.bundles.android.core)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
}