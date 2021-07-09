plugins {
    id("maven-publish")
}

dependencies {
    compileOnly("org.spongepowered", "configurate-hocon", "4.1.1")
    compileOnly("com.google.guava", "guava", "30.1.1-jre")
    compileOnly("net.kyori", "adventure-api", "4.8.1")
    compileOnly("net.kyori", "adventure-serializer-configurate4", "4.8.1")
    api("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT") {
        exclude("net.kyori", "adventure-api")
    }
    compileOnly("org.checkerframework", "checker-qual", "3.15.0")
}

tasks {
    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.links(
                "https://docs.oracle.com/en/java/javase/16/docs/api/",
                "https://configurate.aoeu.xyz/4.1.1/apidocs/",
                "https://javadoc.io/doc/net.kyori/adventure-api/latest/index.html"
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
