import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.util.Constants
import io.papermc.paperweight.util.registering

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.patcher") version "1.1.7"
    id("xyz.jpenilla.run-paper") version "1.0.3"
}

val mojangMappedServer: Configuration by configurations.creating
configurations.compileOnly {
    extendsFrom(mojangMappedServer)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    mavenLocal()
}

dependencies {
    api(project(":minecommons-core"))
    mojangMappedServer("io.papermc.paper", "paper", "1.17-R0.1-SNAPSHOT", classifier = "mojang-mapped")
    remapper("org.quiltmc", "tiny-remapper", "0.4.1")
    // From library loader
    compileOnly("org.spongepowered", "configurate-hocon", "4.1.1")
    val cloudVersion = "1.4.0"
    compileOnly("cloud.commandframework", "cloud-paper", cloudVersion)
    compileOnly("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
    // Plugins
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.6.0")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}-mojang-mapped.jar")
        archiveClassifier.set("mojang-mapped")
        from(rootProject.projectDir.resolve("LICENSE"))
        listOf(
                "cloud.commandframework",
                "io.leangen.geantyref",
                "net.kyori.adventure.text.minimessage",
                "org.bstats",
                "com.github.stefvanschie.inventoryframework"
        ).forEach { relocate(it, "${rootProject.group}.lib.$it") }
    }

    val productionMappedJar by registering<RemapJar> {
        inputJar.set(shadowJar.flatMap { it.archiveFile })
        outputJar.set(project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar"))
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
        minecraftVersion("1.17")
        pluginJars.from(productionMappedJar.flatMap { it.outputJar })
    }
}

runPaper {
    disablePluginJarDetection()
}