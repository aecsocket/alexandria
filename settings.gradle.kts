enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        id("io.freefair.aggregate-javadoc") version "6.3.0"
        id("io.papermc.paperweight.userdev") version "1.3.4"
    }
}

rootProject.name = "minecommons"

subproject("${rootProject.name}-core") {
    projectDir = file("core")
}
subproject("${rootProject.name}-paper") {
    projectDir = file("paper")
}

inline fun subproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
