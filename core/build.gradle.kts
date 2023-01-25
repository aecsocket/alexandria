repositories {
    maven("https://gitlab.com/api/v4/groups/9631292/-/packages/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(libs.glossaCore)
    implementation(libs.glossaAdventure)

    implementation(libs.configurateCore)
    implementation(libs.configurateExtraKotlin)

    compileOnly(libs.adventureApi)
    implementation(libs.adventureExtraKotlin)

    implementation(libs.cloudCore)

    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.adventureApi)
}
