plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
}

val minecraft = libs.versions.minecraft.get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://gitlab.com/api/v4/groups/9631292/-/packages/maven")
    maven("https://jitpack.io")
}

dependencies {
    api(projects.alexandriaCore)
    paperDevBundle("$minecraft-R0.1-SNAPSHOT")

    compileOnly(libs.glossaCore)
    compileOnly(libs.glossaAdventure)
    compileOnly(libs.glossaConfigurate)

    compileOnly(libs.configurateCore)
    compileOnly(libs.configurateHocon)
    compileOnly(libs.configurateExtraKotlin)

    compileOnly(libs.cloudPaper)
    compileOnly(libs.cloudMinecraftExtras) { isTransitive = false }

    compileOnly(libs.adventureApi)
    compileOnly(libs.adventureExtraKotlin)

    compileOnly(libs.packetEventsApi)

    testImplementation(kotlin("test"))
}
