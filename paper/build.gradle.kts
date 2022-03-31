plugins {
    id("java-library")
    id("maven-publish")
    id("io.papermc.paperweight.userdev")
}

val mcVersion = libs.versions.minecraft.get()

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    mavenCentral()
    maven("https://jitpack.io")
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
    api(libs.packetEvents) {
        exclude("net.kyori")
    }
}

tasks {
    jar {
        archiveClassifier.set("jar")
    }
}

fun addReobfTo(target: NamedDomainObjectProvider<Configuration>) {
    target.get().let {
        it.outgoing.artifact(tasks.reobfJar.get().outputJar) {
            classifier = "reobf"
        }
        (components["java"] as AdhocComponentWithVariants).addVariantsFromConfiguration(it) {}
    }
}

addReobfTo(configurations.apiElements)
addReobfTo(configurations.runtimeElements)

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
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
