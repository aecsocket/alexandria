enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.0"
        id("io.papermc.paperweight.userdev") version "1.3.2"
        id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
        id("xyz.jpenilla.run-paper") version "1.0.6"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://repo.dmulloy2.net/nexus/repository/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
    }
}

rootProject.name = "minecommons"

subproject("${rootProject.name}-core") {
    projectDir = file("core");
}
subproject("${rootProject.name}-paper") {
    projectDir = file("paper");
}

inline fun subproject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name);
    project(":$name").apply(block);
}
