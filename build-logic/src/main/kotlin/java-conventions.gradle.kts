plugins {
    id("base-conventions")
    id("java-library")
    id("net.kyori.indra")
}

indra {
    javaVersions {
        target(17)
    }
}

repositories {
    if (!ci.get()) mavenLocal()
    mavenCentral()
}

tasks {
    processResources {
        listOf(
            "fabric.mod.json",
        ).forEach { pattern ->
            filesMatching(pattern) {
                expand(
                    "version" to project.version,
                    "group" to project.group,
                    "description" to project.description
                )
            }
        }
    }
}
