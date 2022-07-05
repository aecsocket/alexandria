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
    api(libs.adventureTextLoggerSlf4j)
    api(libs.glossaAdventure)
    api(libs.glossaConfigurate)
    api(libs.configurateCore)
    api(libs.configurateExtraKotlin)
    api(libs.cloudCore)

    testImplementation(kotlin("test"))
}
