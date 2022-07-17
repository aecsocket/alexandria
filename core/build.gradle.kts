plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://gitlab.com/api/v4/groups/9631292/-/packages/maven")
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
