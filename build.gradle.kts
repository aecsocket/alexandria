plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

allprojects {
    group = "com.gitlab.aecsocket.alexandria"
    version = "0.5.2"
    description = "Platform-agnostic utilities for Minecraft projects"
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }

        test {
            useJUnitPlatform()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
            filter { it
                .replace("@version@", project.version.toString())
                .replace("@description@", project.description.toString())
                .replace("@group@", project.group.toString())
                .replace("@kotlin-version@", libs.versions.kotlin.get())
                .replace("@hikaricp-version@", libs.versions.hikariCp.get())
                .replace("@h2-version@", libs.versions.h2.get())
                .replace("@libbulletjme-version@", libs.versions.libBulletJme.get())
            }
        }
    }

    publishing {
        repositories {
            maven {
                url = uri("${System.getenv("CI_API_V4_URL")}/projects/${System.getenv("CI_PROJECT_ID")}/packages/maven")
                credentials(HttpHeaderCredentials::class) {
                    name = "Job-Token"
                    value = System.getenv("CI_JOB_TOKEN")
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}
