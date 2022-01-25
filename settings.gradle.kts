enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        id("io.freefair.aggregate-javadoc-jar") version "6.3.0"
        id("org.ajoberstar.git-publish") version "3.0.0"

        id("com.github.johnrengelman.shadow") version "7.1.0"
        id("io.papermc.paperweight.userdev") version "1.3.2"
        id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
        id("xyz.jpenilla.run-paper") version "1.0.6"
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
