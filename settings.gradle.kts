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
include(":feature:add_record")
include(":feature:reports")
include(":feature:add_account")
