enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    includeBuild("build-logic")
}

rootProject.name = "alexandria"

include("alexandria-api")
include("alexandria-paper")
