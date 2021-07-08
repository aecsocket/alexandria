# Minecommons

Commons library for Minecraft-related projects, with features such as vector manipulation, event dispatchers and serialization support.

---

## Development Setup

### Dependencies

*  [Java >=16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot)

### Coordinates

#### Maven

Repository
```xml
<repository>
    <id>gitlab-maven-minecommons</id>
    <url>https://gitlab.com/api/v4/projects/27049637/packages/maven</url>
</repository>
```
Dependency
```xml
<dependency>
    <groupId>com.gitlab.aecsocket.minecommons</groupId>
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
implementation("com.gitlab.aecsocket.minecommons", "[MODULE]", "[VERSION]")
```

### Usage

#### [Javadoc](https://aecsocket.gitlab.io/minecommons)

### Modules

* Core

Implementations:
* Paper

## Paper

Minecommons is exposed as a Paper plugin, for other plugins to depend on.

### Dependencies

* [Java >=16](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot)
* [Paper >=1.17.1](https://papermc.io)

### [Download](https://gitlab.com/api/v4/projects/27049637/jobs/artifacts/master/raw/paper/build/libs/minecommons-paper-1.2-SNAPSHOT.jar?job=build)
