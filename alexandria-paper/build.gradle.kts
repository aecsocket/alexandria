plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

val minecraft: String = libs.versions.minecraft.get()

repositories {
    maven("https://repo.codemc.io/repository/maven-snapshots/") // PacketEvents
}

dependencies {
    paperweight.foliaDevBundle("$minecraft-R0.1-SNAPSHOT")
    api(projects.alexandriaApi)
    api(libs.configurateExtraKotlin)
    api(libs.configurateYaml)
    api(libs.adventureSerializerConfigurate)
    api(libs.klamConfigurate)
    api(libs.cloudPaper)
    api(libs.cloudMinecraftExtras)
    api(libs.glossaConfigurate)
    api(libs.packetEventsSpigot)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        exclude("plugin.yml")
    }
}
