plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
}

val minecraftVersion = libs.versions.minecraft.get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    api(projects.alexandriaCore)
    api(libs.configurateHocon)
    api(libs.cloudPaper)
    api(libs.cloudMinecraftExtras) { isTransitive = false }
    compileOnly(libs.packetEventsApi)

    testImplementation(kotlin("test"))
}

fun addReobfTo(target: NamedDomainObjectProvider<Configuration>) {
    target.get().let {
        it.outgoing.artifact(tasks.reobfJar.get().outputJar) {
            classifier = "reobf"
        }
    }
}

addReobfTo(configurations.apiElements)
addReobfTo(configurations.runtimeElements)
