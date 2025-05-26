pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Added JitPack
    }
}

rootProject.name = "BudgetBoss"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":common")
include(":uikit")
include(":feature:home")
include(":feature:record:create")
include(":feature:record:details")
include(":feature:reports")
include(":feature:account:create")
include(":feature:account:details")
include(":feature:category_details")
include(":feature:settings")
