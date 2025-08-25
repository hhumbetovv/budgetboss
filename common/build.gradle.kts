plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.common"
}

dependencies {

    //? Bundles
    implementation(libs.bundles.android.core)

    //? Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
}