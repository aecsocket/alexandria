<div align="center">

<a href="https://aecsocket.gitlab.io/minecommons"><img src="banner.png" width="1024" alt="Minecommons banner" /></a>

`1.4.0-SNAPSHOT`:
[![build](https://github.com/aecsocket/minecommons/actions/workflows/build.yml/badge.svg)](https://github.com/aecsocket/minecommons/actions/workflows/build.yml)

</div>

Common utilities for Minecraft-related projects, split into
implementations based on platform.

# Usage

## Packages

Using any package from the GitHub Packages registry requires you to
authorize with GitHub Packages.

**Note: Never include your token directly in your build scripts!**

<details>
<summary>Maven</summary>

### [How to authorize](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

Repository
```xml
<repositories>
  <repository>
    <id>github-minecommons</id>
    <url>https://maven.pkg.github.com/aecsocket/minecommons</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>

<!-- ... -->

<servers>
  <server>
    <id>github-minecommons</id>
    <username>USERNAME</username>
    <password>TOKEN</password>
  </server>
</servers>
```

Dependency
```xml
<dependencies>
  <dependency>
    <groupId>com.github.aecsocket</groupId>
    <artifactId>minecommons-[module]</artifactId>
    <version>[version]</version>
  </dependency>
</dependencies>
```

</details>

<details>
<summary>Gradle</summary>

The Kotlin DSL is used here.

### [How to authorize](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)

Repository
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/aecsocket/minecommons")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}
```

Dependency
```kotlin
dependencies {
    compileOnly("com.github.aecsocket", "minecommons-[module]", "[version]")
}
```

</details>

## Documentation

### [Javadoc](https://aecsocket.github.io/minecommons/docs)

# Modules

Select which module is appropriate for your platform.

<details>
<summary>Core <code>minecommons-core</code></summary>

This contains platform-independent code, shared among all implementations.

### Dependencies

* [Java >=17](https://adoptium.net/)

</details>

<details>
<summary>Paper <code>minecommons-paper</code></summary>

For the Paper platform.

### Dependencies

* [Java >=17](https://adoptium.net/)
* [Paper >=1.18.1](https://papermc.io/)

</details>
