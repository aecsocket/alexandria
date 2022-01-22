plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")

    id("io.papermc.paperweight.userdev")
    id("net.minecrell.plugin-yml.bukkit")
    id("xyz.jpenilla.run-paper")
}

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
}

dependencies {
    implementation(project(":minecommons-core"))
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    compileOnlyApi(libs.bundles.paperCloud)
    compileOnly(libs.paperProtocolLib)
    library(libs.bundles.paperLibs)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    build {
        dependsOn(reobfJar)
    }

    runServer {
        minecraftVersion("1.18.1")
    }
}

bukkit {
    name = "Minecommons"
    main = "${project.group}.${rootProject.name}.paper.MinecommonsPlugin"
    apiVersion = "1.18"
    softDepend = listOf("ProtocolLib")
    website = "https://github.com/aecsocket/minecommons"
    authors = listOf("aecsocket")
}
