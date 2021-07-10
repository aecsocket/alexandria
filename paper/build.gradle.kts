import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.util.Constants
import io.papermc.paperweight.util.registering

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.patcher") version "1.1.7"
    id("xyz.jpenilla.run-paper") version "1.0.3"
}

val minecraftVersion = "1.17.1"

val mojangMappedServer: Configuration by configurations.creating
configurations.compileOnly {
    extendsFrom(mojangMappedServer)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.quiltmc.org/repository/release/") {
        mavenContent {
            releasesOnly()
            includeModule("org.quiltmc", "tiny-remapper")
        }
    }
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    mavenLocal()
}

dependencies {
    api(project(":core"))
    mojangMappedServer("io.papermc.paper", "paper", "1.17.1-R0.1-SNAPSHOT", classifier = "mojang-mapped") {
        exclude("junit", "junit")
    }
    remapper("org.quiltmc", "tiny-remapper", "0.4.1")
    // From library loader
    compileOnly("org.spongepowered", "configurate-hocon", "4.1.1")
    val cloudVersion = "1.4.0"
    compileOnly("cloud.commandframework", "cloud-paper", cloudVersion)
    compileOnly("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
    compileOnly("com.github.stefvanschie.inventoryframework", "IF", "0.10.0")
    // Plugins
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0")
}

tasks {
    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.links(
                "https://docs.oracle.com/en/java/javase/16/docs/api/",
                "https://configurate.aoeu.xyz/4.1.1/apidocs/",
                "https://jd.adventure.kyori.net/api/4.8.1/",
                "https://papermc.io/javadocs/paper/1.17/",
                "https://javadoc.commandframework.cloud/",
                "https://aadnk.github.io/ProtocolLib/Javadoc/"
        )
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}-mojang-mapped.jar")
        archiveClassifier.set("mojang-mapped")
    }

    val productionMappedJar by registering<RemapJar> {
        inputJar.set(shadowJar.flatMap { it.archiveFile })
        outputJar.set(project.layout.buildDirectory.file("libs/${rootProject.name}-${project.name}-${rootProject.version}.jar"))
        mappingsFile.set(project.layout.projectDirectory.file("mojang+yarn-spigot-reobf-patched.tiny"))
        fromNamespace.set(Constants.DEOBF_NAMESPACE)
        toNamespace.set(Constants.SPIGOT_NAMESPACE)
        remapper.from(project.configurations.remapper)
        remapClasspath.from(mojangMappedServer)
    }

    build {
        dependsOn(productionMappedJar)
    }

    runServer {
        minecraftVersion(minecraftVersion)
        pluginJars.from(productionMappedJar.flatMap { it.outputJar })
    }
}

runPaper {
    disablePluginJarDetection()
}

publishing {
    publications {
        create<MavenPublication>("gitlab") {
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
