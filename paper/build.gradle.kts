plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.0.0"

    id("io.papermc.paperweight.userdev") version "1.1.14"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("xyz.jpenilla.run-paper") version "1.0.4"
}

val exposedApi: Configuration by configurations.creating {
    isTransitive = true
}

configurations.compileOnlyApi {
    extendsFrom(exposedApi)
}

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    mavenLocal()
}

dependencies {
    api(project(":core"))
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")

    val cloudVersion = "1.5.0"

    exposedApi("cloud.commandframework", "cloud-paper", cloudVersion)
    exposedApi("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
    // Plugins
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0")
    // Library loader
    library("org.spongepowered", "configurate-hocon", "4.1.1")
    library("net.sf.opencsv", "opencsv", "2.3")
    library("net.kyori", "adventure-serializer-configurate4", "4.9.3")
    library("cloud.commandframework", "cloud-paper", cloudVersion)
    library("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
}

tasks {
    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.links(
                "https://docs.oracle.com/en/java/javase/17/docs/api/",
                "https://guava.dev/releases/snapshot-jre/api/docs/",
                "https://configurate.aoeu.xyz/4.1.2/apidocs/",
                "https://jd.adventure.kyori.net/api/4.9.3/",
                "https://www.javadoc.io/doc/io.leangen.geantyref/geantyref/1.3.11/",

                "https://papermc.io/javadocs/paper/1.17/",
                "https://javadoc.commandframework.cloud/",
                "https://aadnk.github.io/ProtocolLib/Javadoc/"
        )
    }

    jar {
        //archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}.jar")
    }

    shadowJar {
        //archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}.jar")
        /*listOf(
                //"net.kyori.adventure.text.minimessage",
                "org.incendo.interfaces"
        ).forEach { relocate(it, "${rootProject.group}.lib.$it") }*/
    }

    // reobfJar must depend on shadowJar
    //reobfJar {
    //    dependsOn(jar)
    //    dependsOn(shadowJar)
    //}

    assemble {
        dependsOn(shadowJar)
    }

    build {
        dependsOn(reobfJar)
    }
}

bukkit {
    name = "Minecommons"
    main = "${project.group}.paper.MinecommonsPlugin"
    apiVersion = "1.17"
    softDepend = listOf("ProtocolLib")
    website = "https://gitlab.com/aecsocket/minecommons"
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
            url = uri("https://gitlab.com/api/v4/projects/27049637/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}
