enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.fabricmc.net")
    }
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

rootProject.name = "alexandria-parent"

include("alexandria-api")
include("alexandria-common")
include("alexandria-paper")
// don't include fabric build in the CI because it eats too much RAM and crashes
// TODO: make this work lol
if (!providers.environmentVariable("CI").map { it.toBoolean() }.getOrElse(false)) {
    include("alexandria-fabric")
}
