rootProject.name = "SA"
include(":app")
include(":core")
include(":domain")
include(":data")
include(":feature:repositories")
include(":feature:repositorydetail")

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
    // Version catalog is now defined in the gradle/libs.versions.toml file
} 
