plugins {
    id("maven-publish")
}

dependencies {
    compileOnlyApi(libs.bundles.base)
    compileOnlyApi(libs.bundles.adventure)
    api(libs.adventureTextMiniMessage)

    testImplementation(libs.bundles.junit)
    testImplementation(libs.adventureTextSerializerGson)
    testRuntimeOnly(libs.geantyRef)
    testRuntimeOnly(libs.bundles.adventure)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
