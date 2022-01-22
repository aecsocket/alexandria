<img src="banner.png" width="1024" alt="Minecommons"/>

Common utilities for Minecraft-related projects, with features such as vector manipulation,
event dispatchers and serialization support.

---

## Paper

Minecommons is exposed as a Paper plugin, for other plugins to depend on.

### Dependencies

* [Java >=17](https://adoptium.net/)
* [Paper >=1.18.1](https://papermc.io)

### Download Version 1.4 (NOT WORKING)

## Development Setup

### Coordinates

#### Maven

# THESE INSTRUCTIONS DO NOT WORK

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

#### Javadoc (NOT WORKING)

### Modules

* Core `minecommons-core`

Implementations:
* Paper `minecommons-paper`
