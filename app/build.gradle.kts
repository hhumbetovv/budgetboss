plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

apply(from = "$rootDir/base-build.gradle")

android {
    namespace = "com.theternal.budgetboss"

    defaultConfig {
        applicationId = "com.theternal.budgetboss"
        versionCode = 3
        versionName = "1.2"
    }
}

dependencies {
    //! Modules
    implementation(projects.common)
    implementation(projects.uikit)
    implementation(projects.core)

    implementation(projects.feature.home)
    implementation(projects.feature.record.create)
    implementation(projects.feature.reports)
    implementation(projects.feature.settings)

    //? Bundles
    implementation(libs.bundles.android.core)

    //? Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //? Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //? Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

}