plugins {
    id("java-library")
    id("maven-publish")
}

allprojects {
    group = "com.gitlab.aecsocket"
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
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(16)
        }
        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }

        test {
            useJUnitPlatform()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("minecommons-core"){
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
