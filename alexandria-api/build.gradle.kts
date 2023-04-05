plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
}

dependencies {
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.adventureApi)
    implementation(libs.adventureSerializerConfigurate)
    implementation(libs.klam)
    implementation(libs.klamConfigurate)
    implementation(libs.configurateCore)
    implementation(libs.cloudCore)
    implementation(libs.glossaApi)
    implementation(libs.glossaConfigurate)
}
