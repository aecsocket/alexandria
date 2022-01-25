plugins {
    id("java-library")
    id("maven-publish")
    id("io.freefair.aggregate-javadoc-jar")
}

group = "com.github.aecsocket"
version = "1.4.0-SNAPSHOT"
description = "Common utilities for Minecraft projects"

tasks.named("aggregateJavadoc").configure {
    val opt = (this as Javadoc).options as StandardJavadocDocletOptions
    opt.encoding = "UTF-8"
    opt.addBooleanOption("html5", true)
    opt.addStringOption("-release", "17")
    opt.linkSource()
    opt.links(
            "https://docs.oracle.com/en/java/javase/17/docs/api/",
            "https://guava.dev/releases/snapshot-jre/api/docs/",
            "https://configurate.aoeu.xyz/4.1.2/apidocs/",
            "https://jd.adventure.kyori.net/api/4.9.3/",
            "https://www.javadoc.io/doc/io.leangen.geantyref/geantyref/1.3.11/",

            "https://papermc.io/javadocs/paper/1.18/",
            "https://javadoc.commandframework.cloud/",
            "https://aadnk.github.io/ProtocolLib/Javadoc/"
    )
}

subprojects {
    apply<JavaLibraryPlugin>()

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }

        test {
            useJUnitPlatform()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/aecsocket/minecommons")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_ACTOR")
                password = project.findProject("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
            }
        }
    }
}
