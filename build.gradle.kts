plugins {
    id("java-library")
    id("maven-publish")
    id("io.freefair.aggregate-javadoc")
}

allprojects {
    group = "com.github.aecsocket"
    version = "1.4.2-SNAPSHOT"
    description = "Common utilities for Minecraft projects"
}

tasks.aggregateJavadoc {
    val opt = this.options as StandardJavadocDocletOptions
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
        "https://packetevents.github.io/javadocs/"
    )
}

subprojects {
    apply<JavaLibraryPlugin>()

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withSourcesJar()
        withJavadocJar()
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
