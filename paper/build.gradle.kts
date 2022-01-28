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
    mavenCentral()
}

dependencies {
    api(projects.minecommonsCore)
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    // Plugins + library loader
    compileOnly(libs.bundles.cloudPaper)
    compileOnly(libs.protocolLib)
    library(libs.bundles.libsPaper)
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/aecsocket/minecommons")
            credentials {
                username = System.getenv("GPR_ACTOR")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
}
