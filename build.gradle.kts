plugins {
    id("java-library")
    id("maven-publish")
}

allprojects {
    group = "com.gitlab.aecsocket.minecommons"
    version = "1.2-SNAPSHOT"
    description = "Commons library for Minecraft"
}

subprojects {
    apply<JavaLibraryPlugin>()

    java {
        targetCompatibility = JavaVersion.toVersion(16)
        sourceCompatibility = JavaVersion.toVersion(16)
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://maven.quiltmc.org/repository/release/") {
            mavenContent {
                releasesOnly()
                includeModule("org.quiltmc", "tiny-remapper")
            }
        }
        mavenLocal()
    }

    dependencies {
        implementation("org.jetbrains", "annotations", "16.0.2")
        testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.1")
    }

    tasks {
        jar {
            archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}.jar")
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(16)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
            source = sourceSets.main.get().allJava
        }

        test {
            useJUnitPlatform()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("gitlab") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "gitlabMinecommons"
            url = uri("https://gitlab.com/api/v4/projects/27049637/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}
