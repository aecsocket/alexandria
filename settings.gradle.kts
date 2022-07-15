enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        kotlin("jvm") version "1.6.21"
        id("org.jetbrains.dokka") version "1.6.21"

        id("io.papermc.paperweight.userdev") version "1.3.6"
    }
}

rootProject.name = "alexandria"

listOf(
    "core",
    "paper",
).forEach {
    val name = "${rootProject.name}-$it"
    include(name)
    project(":$name").apply {
        projectDir = file(it)
    }
}
