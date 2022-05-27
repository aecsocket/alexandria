plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(libs.adventureApi)
    implementation(libs.adventureExtraKotlin)
    api(libs.configurateCore)
    api(libs.configurateExtraKotlin)

    testImplementation(kotlin("test"))
}
