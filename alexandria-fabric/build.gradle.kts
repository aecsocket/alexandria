plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    alias(libs.plugins.fabric.loom)
}

repositories {
    sonatype.ossSnapshots()
}

dependencies {
    minecraft("com.mojang", "minecraft", "1.20.1")
    mappings(loom.officialMojangMappings())
    api(projects.alexandriaCommon)

    modImplementation(libs.fabric.loader)
    modApi(libs.adventure.platform.fabric)
    include(libs.adventure.platform.fabric)
    modApi(libs.cloud.fabric)
    include(libs.cloud.fabric)
}
