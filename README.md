<img src="banner.png" width="1024"/>

# Minecommons

Common utilities for Minecraft-related projects, with features such as vector manipulation,
event dispatchers and serialization support.

---

## Paper

Minecommons is exposed as a Paper plugin, for other plugins to depend on.

### Dependencies

* [Java >=17](https://adoptium.net/)
* [Paper >=1.18.1](https://papermc.io)

### [Download Version 1.4](https://gitlab.com/api/v4/projects/27049637/jobs/artifacts/master/raw/minecommons-paper/build/libs/minecommons-paper-1.4.jar?job=build)

## Development Setup

### Coordinates

#### Maven

Repository
```xml
<repository>
    <id>minecommons</id>
    <url>https://gitlab.com/api/v4/projects/27049637/packages/maven</url>
</repository>
```

Dependency
```xml
<dependency>
    <groupId>com.gitlab.aecsocket</groupId>
    <artifactId>[MODULE]</artifactId>
    <version>[VERSION]</version>
</dependency>
```

#### Gradle

Repository
```kotlin
maven("https://gitlab.com/api/v4/projects/27049637/packages/maven")
```

Dependency
```kotlin
implementation("com.gitlab.aecsocket", "[MODULE]", "[VERSION]")
```

### Usage

#### [Javadoc](https://aecsocket.gitlab.io/minecommons)

### Modules

* Core `minecommons-core`

Implementations:
* Paper `minecommons-paper`
