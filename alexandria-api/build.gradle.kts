plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
}

dependencies {
    api(libs.kotlinxCoroutinesCore)
    api(libs.klam)
    api(libs.configurateCore)
    api(libs.cloudCore)
    api(libs.glossa)
}
