plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")

    id("io.papermc.paperweight.userdev")
    id("net.minecrell.plugin-yml.bukkit")
    id("xyz.jpenilla.run-paper")
}

val pluginName = "Minecommons"

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
}

dependencies {
    api(project(":core"))
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    compileOnlyApi(libs.bundles.paperCloud)
    compileOnly(libs.paperProtocolLib)
    library(libs.bundles.paperLibs)
}

tasks {
    javadoc {
        val opt = options as StandardJavadocDocletOptions
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

    base {
        archivesName.set(pluginName)
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${pluginName}-${version}.jar"))
    }

    assemble {
        dependsOn(shadowJar)
    }

    build {
        dependsOn(reobfJar)
    }

    runServer {
        minecraftVersion("1.18.1")
    }
}

bukkit {
    name = pluginName
    main = "${project.group}.paper.${pluginName}Plugin"
    apiVersion = "1.18"
    softDepend = listOf("ProtocolLib")
    website = "https://gitlab.com/aecsocket/minecommons"
    authors = listOf("aecsocket")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
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
