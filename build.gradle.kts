// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    //! Versions
    val roomVersion = "2.6.1"
    val hiltVersion = "2.49"

    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.android.library") version "8.2.2" apply false
    id("androidx.room") version roomVersion apply false
    id("com.google.dagger.hilt.android") version hiltVersion apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false

}