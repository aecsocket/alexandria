plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
}

val minecraftVersion = libs.versions.minecraft.get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    api(projects.alexandriaCore)
    api(libs.glossaAdventure)
    api(libs.glossaConfigurate)
    implementation(libs.adventureExtraKotlin)
    implementation(libs.configurateHocon)
    api(libs.cloudPaper)
    api(libs.cloudMinecraftExtras) { isTransitive = false }
    compileOnly(libs.packetEvents)

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
