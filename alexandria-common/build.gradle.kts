plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
}

dependencies {
    api(projects.alexandriaApi)
    api(libs.configurate.yaml)
    api(libs.configurate.toml)
}
