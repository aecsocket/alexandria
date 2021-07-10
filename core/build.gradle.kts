plugins {
    id("maven-publish")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

val exposedApi: Configuration by configurations.creating {
    isTransitive = true
}
configurations.compileOnlyApi {
    extendsFrom(exposedApi)
}

dependencies {
    exposedApi("org.spongepowered", "configurate-hocon", "4.1.1")
    exposedApi("com.google.guava", "guava", "30.1.1-jre")
    exposedApi("net.kyori", "adventure-api", "4.8.1")
    exposedApi("net.kyori", "adventure-serializer-configurate4", "4.8.1")
    exposedApi("org.checkerframework", "checker-qual", "3.15.0")
    api("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT") {
        exclude("net.kyori", "adventure-api")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.links(
                "https://docs.oracle.com/en/java/javase/16/docs/api/",
                "https://configurate.aoeu.xyz/4.1.1/apidocs/",
                "https://jd.adventure.kyori.net/api/4.8.1/"
        )
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
