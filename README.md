<div align="center">

<img src="banner.png" width="1024" alt="Minecommons banner" />

`1.4.0-SNAPSHOT`:
[![Workflow status](https://img.shields.io/github/workflow/status/aecsocket/minecommons/build?style=flat-square)](https://github.com/aecsocket/demeter/actions)

</div>

Common utilities for Minecraft-related projects, split into
implementations based on platform.

# Usage

### [Documentation](https://aecsocket.github.io/minecommons)

### Usage

Using any package from the GitHub Packages registry requires you to
authorize with GitHub Packages.

#### Repository

Maven
```xml
<repository>
  <id>github-minecommons</id>
  <url>https://maven.pkg.github.com/aecsocket/minecommons</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>

<!-- ... -->

<servers>
  <server>
    <id>github-minecommons</id>
    <username>USERNAME</username>
    <password>TOKEN</password>
  </server>
</servers>
```

Gradle
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

#### Coordinates

Maven
```xml
<dependency>
  <groupId>com.github.aecsocket</groupId>
  <artifactId>minecommons-[module]</artifactId>
  <version>[version]</version>
</dependency>
```

Gradle
```kotlin
compileOnly("com.github.aecsocket", "minecommons-[module]", "[version]")
```

# Modules

Select which module is appropriate for your platform.

<details>
<summary>Core <code>core</code></summary>

### Dependencies

* [Java >=17](https://adoptium.net/)

</details>

<details>
<summary>Paper <code>paper</code></summary>

### Dependencies

* [Java >=17](https://adoptium.net/)
* [Paper >=1.18.1](https://papermc.io/)

</details>
