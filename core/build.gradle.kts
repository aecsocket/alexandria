plugins {
    id("maven-publish")
}

val exposedApi: Configuration by configurations.creating {
    isTransitive = true
}
configurations.compileOnlyApi {
    extendsFrom(exposedApi)
}

dependencies {
    val adventureVersion = "4.9.3"

    exposedApi("com.google.guava", "guava", "30.1.1-jre")
    exposedApi("org.spongepowered", "configurate-hocon", "4.1.2")
    exposedApi("net.kyori", "adventure-api", adventureVersion)
    exposedApi("net.kyori", "adventure-serializer-configurate4", adventureVersion)
    exposedApi("net.kyori", "adventure-text-serializer-plain", adventureVersion)
    exposedApi("org.checkerframework", "checker-qual", "3.15.0")
    exposedApi("io.leangen.geantyref", "geantyref", "1.3.11")
    testRuntimeOnly("io.leangen.geantyref", "geantyref", "1.3.11")
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
                "https://docs.oracle.com/en/java/javase/17/docs/api/",
                "https://guava.dev/releases/snapshot-jre/api/docs/",
                "https://configurate.aoeu.xyz/4.1.2/apidocs/",
                "https://jd.adventure.kyori.net/api/4.9.3/",
                "https://www.javadoc.io/doc/io.leangen.geantyref/geantyref/1.3.11/"
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
