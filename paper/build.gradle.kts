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

    implementation(libs.configurateCore)
    implementation(libs.configurateHocon)
    implementation(libs.configurateExtraKotlin)

    implementation(libs.cloudPaper)
    implementation(libs.cloudMinecraftExtras) { isTransitive = false }

    compileOnly(libs.adventureApi)
    implementation(libs.adventureExtraKotlin)
    implementation(libs.adventureSerializerConfigurate)

    implementation(libs.packetEventsSpigot)

    compileOnly(libs.ktRuntime)

    // library loader

    implementation(libs.ktRuntime)

    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    shadowJar {
        mergeServiceFiles()
        // can't exclude `net/kyori/` here beacuse of glossa's configurate component serializer
        // but otherwise packetevents will include adventure-api here
        listOf(
            "org.jetbrains",
            "org.intellij",
        ).forEach { relocate(it, "${project.group}.lib.$it") }

        exclude("kotlin/")
        exclude("kotlinx/")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(minecraft)
    }
}
