import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

allprojects {
    group = "com.github.aecsocket.alexandria"
    version = "0.3.4"
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "java-library")
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

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(18))
        }
    }
}
