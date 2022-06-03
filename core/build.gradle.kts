plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api(libs.adventureApi)
    api(libs.adventureExtraKotlin)
    api(libs.glossaAdventure)
    api(libs.glossaConfigurate)
    api(libs.configurateCore)
    api(libs.configurateExtraKotlin)

    testImplementation(kotlin("test"))
}
