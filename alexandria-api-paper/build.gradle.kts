plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

val minecraft: String = libs.versions.minecraft.get()

dependencies {
    implementation(projects.alexandriaApi)
    paperweight.foliaDevBundle("$minecraft-R0.1-SNAPSHOT")
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.klam)
    implementation(libs.configurateCore)
    implementation(libs.configurateExtraKotlin)
    implementation(libs.configurateYaml)
    implementation(libs.cloudCore)
    implementation(libs.cloudPaper)
    implementation(libs.cloudMinecraftExtras)
    implementation(libs.glossaApi)
    implementation(libs.glossaConfigurate)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
}
