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
include(":feature:record:add")
include(":feature:record:details")
include(":feature:reports")
include(":feature:account:add")
include(":feature:account:details")
include(":feature:category_details")
include(":feature:settings")
