plugins {
    id("maven-publish")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    compileOnly(libs.checkerQual)
    api(libs.geantyRef)
    api(libs.configurate)
    api(libs.openCsv)

    api(libs.bundles.adventure)
    api(libs.adventureTextMiniMessage)

    testImplementation(libs.bundles.junit)
    testImplementation(libs.adventureTextSerializerGson)
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
