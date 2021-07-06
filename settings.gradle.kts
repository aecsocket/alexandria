pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "Minecommons"

include("core", "paper")

project(":core").name = "minecommons-core"
project(":paper").name = "minecommons-paper"
