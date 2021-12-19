enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        id("java-library")
        id("maven-publish")
        id("com.github.johnrengelman.shadow") version "7.1.0"

        id("io.papermc.paperweight.userdev") version "1.3.2"
        id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
        id("xyz.jpenilla.run-paper") version "1.0.6"
    }
}

rootProject.name = "minecommons"

include("core", "paper")
