plugins {
    id("java-library")
    id("maven-publish")
    id("io.papermc.paperweight.userdev")
}

val mcVersion = libs.versions.minecraft.get()

repositories {
    maven("https://repo.incendo.org/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    mavenCentral()
}

dependencies {
    api(projects.minecommonsCore) {
        exclude("com.google.guava", "guava")
        exclude("org.checkerframework", "checker-qual")
        exclude("net.kyori", "adventure-api")
        exclude("net.kyori", "adventure-text-serializer-gson")
        exclude("net.kyori", "adventure-text-serializer-plain")
    }
    paperDevBundle("$mcVersion-R0.1-SNAPSHOT")

    api(libs.inventoryFramework)
    api(libs.cloudPaper)
    api(libs.cloudExtras) {
        isTransitive = false
    }

    compileOnly(libs.protocolLib)
}

/*
  TODO this is kind of a hack.
  If this isn't here, gradle makes a .module file, which
  forces dependents to use the -dev jar, which is
  remapped, so cannot be shaded in without a mojmap
  server.
 */
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks.reobfJar)
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/aecsocket/minecommons")
            credentials {
                username = System.getenv("GPR_ACTOR")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
}
