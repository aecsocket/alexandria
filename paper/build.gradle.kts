plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

val minecraft = libs.versions.minecraft.get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://gitlab.com/api/v4/groups/9631292/-/packages/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    api(projects.alexandriaCore)
    paperDevBundle("$minecraft-R0.1-SNAPSHOT")

    implementation(libs.glossaCore)
    implementation(libs.glossaAdventure)
    implementation(libs.glossaConfigurate)

    implementation(libs.configurateCore)
    implementation(libs.configurateHocon)
    implementation(libs.configurateExtraKotlin)

    implementation(libs.cloudPaper)
    implementation(libs.cloudMinecraftExtras) { isTransitive = false }

    compileOnly(libs.adventureApi)
    implementation(libs.adventureExtraKotlin)

    implementation(libs.packetEventsSpigot)

    // library loader

    // kotlinStdlib
    compileOnly(libs.kotlinReflect)

    testImplementation(kotlin("test"))
}

tasks {
    shadowJar {
        mergeServiceFiles()
        exclude("kotlin/")
        exclude("kotlinx/")
        // can't exclude `net/kyori/` here beacuse of glossa's configurate component serialize
        // but otherwise packetevents will include adventure-api here
        listOf(
            "org.jetbrains",
            "org.intellij",
        ).forEach { relocate(it, "${project.group}.lib.$it") }
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(minecraft)
    }
}
