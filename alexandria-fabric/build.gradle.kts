plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
    alias(libs.plugins.fabric.loom)
}

repositories {
    sonatype.ossSnapshots()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    api(projects.alexandriaApi)

    modImplementation(libs.fabric.loader)
    modApi(libs.adventure.platform.fabric)
    include(libs.adventure.platform.fabric)
    modApi(libs.cloud.fabric)
    include(libs.cloud.fabric)
}
