plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.configurateCore)
    compileOnly(libs.configurateExtraKotlin)

    compileOnly(libs.adventureApi)
    compileOnly(libs.adventureExtraKotlin)

    compileOnly(libs.cloudCore)

    testImplementation(kotlin("test"))
    testImplementation(libs.adventureApi)
}
