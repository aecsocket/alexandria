plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.configurate.core)
    api(libs.klam)
    api(libs.klam.configurate)
    api(libs.glossa)
    api(libs.glossa.configurate)
    api(libs.cloud.core)
    api(libs.cloud.minecraft.extras)
}
