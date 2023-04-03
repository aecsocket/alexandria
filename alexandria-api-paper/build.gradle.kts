plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

val minecraft = libs.versions.minecraft.get()

dependencies {
    implementation(projects.alexandriaApi)
    paperweight.paperDevBundle("$minecraft-R0.1-SNAPSHOT")
    compileOnly("dev.folia", "folia-api", "$minecraft-R0.1-SNAPSHOT")
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
