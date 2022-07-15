import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

allprojects {
    group = "com.github.aecsocket.alexandria"
    version = "0.4.0"
    description = "Platform-agnostic utilities for Minecraft projects"
}

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<KotlinPlatformJvmPlugin>()
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        test {
            useJUnitPlatform()
        }
    }
}
