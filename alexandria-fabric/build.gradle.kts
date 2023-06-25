plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    alias(libs.plugins.fabric.loom)
}

val minecraft = libs.versions.fabric.asProvider().get()

repositories {
    sonatype.ossSnapshots()
}

dependencies {
    minecraft("com.mojang", "minecraft", minecraft)
    mappings(loom.officialMojangMappings())
    api(projects.alexandriaCommon)

    modImplementation(libs.fabric.loader)
    modApi(libs.adventure.platform.fabric)
    include(libs.adventure.platform.fabric)
    modApi(libs.cloud.fabric)
    include(libs.cloud.fabric)
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "group" to project.group,
            "description" to project.description,
            "versions" to mapOf(
                "fabric" to minecraft,
                "fabric_loader" to libs.versions.fabric.loader.get(),
            ),
        )
    }
}
