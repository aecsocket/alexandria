plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://gitlab.com/api/v4/groups/9631292/-/packages/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(libs.glossaCore)
    implementation(libs.glossaAdventure)
    implementation(libs.glossaConfigurate)

    implementation(libs.configurateCore)
    implementation(libs.configurateExtraKotlin)

    compileOnly(libs.adventureApi)
    implementation(libs.adventureExtraKotlin)

    implementation(libs.cloudCore)

    testImplementation(kotlin("test"))
    testImplementation(libs.adventureApi)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        exclude("kotlin/")
        exclude("kotlinx/")
        listOf(
            "com.gitlab.aecsocket.glossa",
            "com.ibm.icu",

            "org.spongepowered.configurate",
            "io.leangen.geantyref",
            "com.typesafe.config",

            "commandframework.cloud",

            "com.github.retrooper.packetevents",
            "net.kyori",

            "cloud.commandframework",

            "org.jetbrains",
            "org.intellij",
        ).forEach { relocate(it, "${project.group}.lib.$it") }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks["shadowJar"])
        }
    }
}
