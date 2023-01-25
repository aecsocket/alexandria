plugins {
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

val minecraft = libs.versions.minecraft.get()

repositories {
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

    implementation(libs.bstatsBukkit)

    implementation(libs.cloudPaper)
    implementation(libs.cloudMinecraftExtras) { isTransitive = false }

    compileOnly(libs.adventureApi)
    implementation(libs.adventureExtraKotlin) { isTransitive = false }
    implementation(libs.adventureSerializerConfigurate) { isTransitive = false }

    implementation(libs.packetEventsSpigot)

    // library loader
    compileOnly(libs.kotlinxCoroutines)


    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    shadowJar {
        mergeServiceFiles()

        // kt-runtime
        exclude("kotlin/")
        exclude("kotlinx/")

        listOf(
            "org.jetbrains",
            "org.intellij",
            "org.bstats",

            "com.ibm.icu",
            "org.spongepowered.configurate",
            "io.leangen.geantyref",
            "com.typesafe.config",
            "cloud.commandframework",
            "com.github.retrooper.packetevents",
            "io.github.retrooper.packetevents",
        ).forEach { relocate(it, "${project.group}.lib.$it") }
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(minecraft)
    }
}
