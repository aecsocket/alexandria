plugins {
    id("kotlin-conventions")
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

val minecraft = libs.versions.minecraft.get()

dependencies {
    implementation(projects.alexandriaCore)
    paperweight.paperDevBundle("$minecraft-R0.1-SNAPSHOT")
    implementation(libs.configurateCore)
    implementation(libs.configurateExtraKotlin)
    implementation(libs.configurateYaml)
    implementation(libs.cloudCore)
    implementation(libs.cloudPaper)
    implementation(libs.cloudMinecraftExtras)
    implementation(libs.glossaCore)
    implementation(libs.glossaConfigurate)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
}
