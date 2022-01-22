plugins {
    id("maven-publish")
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenCentral()
}

dependencies {
    compileOnlyApi(libs.bundles.base)
    compileOnlyApi(libs.bundles.adventure)
    api(libs.adventureTextMiniMessage)

    testImplementation(libs.bundles.junit)
    testImplementation(libs.adventureTextSerializerGson)
    testRuntimeOnly(libs.geantyRef)
    testRuntimeOnly(libs.bundles.adventure)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.links(
                "https://docs.oracle.com/en/java/javase/17/docs/api/",
                "https://guava.dev/releases/snapshot-jre/api/docs/",
                "https://configurate.aoeu.xyz/4.1.2/apidocs/",
                "https://jd.adventure.kyori.net/api/4.9.3/",
                "https://www.javadoc.io/doc/io.leangen.geantyref/geantyref/1.3.11/"
        )
    }
}

// publishing {
//     publications {
//         create<MavenPublication>("gitlab") {
//             from(components["java"])
//         }
//     }

//     repositories {
//         maven {
//             url = uri("https://gitlab.com/api/v4/projects/27049637/packages/maven")
//             credentials(HttpHeaderCredentials::class) {
//                 name = "Job-Token"
//                 value = System.getenv("CI_JOB_TOKEN")
//             }
//             authentication {
//                 create<HttpHeaderAuthentication>("header")
//             }
//         }
//     }
// }
