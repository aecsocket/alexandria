plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.adventure.api)
    api(libs.adventure.serializer.configurate4)
    api(libs.configurate.core)
    api(libs.configurate.extra.kotlin)
    api(libs.configurate.yaml)
    api(libs.configurate.toml)
    api(libs.klam)
    api(libs.klam.configurate)
    api(libs.glossa)
    api(libs.glossa.configurate)
    api(libs.cloud.core)
    api(libs.cloud.minecraft.extras)
}
