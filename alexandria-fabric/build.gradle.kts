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
    modImplementation(libs.adventure.platform.fabric)
    include(libs.adventure.platform.fabric)
    modImplementation(libs.cloud.fabric)
    include(libs.cloud.fabric)
}
