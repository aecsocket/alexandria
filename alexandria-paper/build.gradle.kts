plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.run.paper)
}

val minecraft: String = libs.versions.paper.get()

repositories {
    maven("https://repo.codemc.io/repository/maven-snapshots/") // PacketEvents
}

dependencies {
    paperweight.foliaDevBundle("$minecraft-R0.1-SNAPSHOT")
    api(projects.alexandriaCommon)

    api(libs.cloud.paper)
    api(libs.packetevents.spigot)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        exclude("plugin.yml")
    }
}
