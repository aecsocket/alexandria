plugins {
    id("java-library")
    id("maven-publish")
    id("io.papermc.paperweight.userdev")
}

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
    paperDevBundle("${libs.versions.minecraft.forUseAtConfigurationTime().get()}-R0.1-SNAPSHOT")

    api(libs.interfacesPaper)
    api(libs.cloudPaper)
    api(libs.cloudExtras) {
        isTransitive = false
    }

    compileOnly(libs.protocolLib)
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
                username = System.getenv("GPR_ACTOR")
                password = System.getenv("GPR_TOKEN")
            }
        }
    }
}
