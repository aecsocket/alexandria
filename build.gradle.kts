plugins {
    id("java-library")
    id("maven-publish")
}

allprojects {
    group = "com.github.aecsocket"
    version = "1.4"
    description = "Common utilities for Minecraft projects"
}

subprojects {
    apply<JavaLibraryPlugin>()

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }

        javadoc {
            val opt = options as StandardJavadocDocletOptions
            opt.encoding = Charsets.UTF_8.name()
            opt.source("17")
            opt.linkSource(true)
            opt.author(true)
        }

        test {
            useJUnitPlatform()
        }
    }
}
