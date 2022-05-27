plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

allprojects {
    group = "com.github.aecsocket.alexandria"
    version = "0.3.2-SNAPSHOT"
    description = "Library"
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}
