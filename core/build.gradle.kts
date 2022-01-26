plugins {
    id("maven-publish")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenCentral()
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

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/aecsocket/minecommons")
            credentials {
                username = System.getenv("GPR_ACTOR")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
}
