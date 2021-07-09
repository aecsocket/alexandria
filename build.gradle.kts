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
            val opt = options as StandardJavadocDocletOptions
            opt.encoding = Charsets.UTF_8.name()
            opt.source("16")
            opt.linkSource(true)
            setDestinationDir(file("${buildDir}/docs/javadoc"))
            title = "${project.name} $version API"
            opt.author(true)
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
