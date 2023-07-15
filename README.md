<div align="center">

# Alexandria
[![CI](https://img.shields.io/github/actions/workflow/status/aecsocket/alexandria/build.yml)](https://github.com/aecsocket/alexandria/actions/workflows/build.yml)
[![Release](https://img.shields.io/maven-central/v/io.github.aecsocket/alexandria-api?label=release)](https://central.sonatype.com/artifact/io.github.aecsocket/alexandria-api)
[![Snapshot](https://img.shields.io/nexus/s/io.github.aecsocket/alexandria-api?label=snapshot&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://central.sonatype.com/artifact/io.github.aecsocket/alexandria-api)

Multiplatform utilities for Minecraft projects

### [GitHub](https://github.com/aecsocket/alexandria) · [Dokka](https://aecsocket.github.io/alexandria/dokka)

</div>

A generic set of utilities for other projects, designed to be multiplatform but also have certain
platform-specific utilities.

There is no user documentation; see the Dokka page to get API docs.

## Usage

See the version badges for the latest release and snapshot builds.

Modules:
- `alexandria-api` - platform-independent API
- `alexandria-common` - common library for implementing the API onto a platform
- `alexandria-paper` - [Paper](https://papermc.io) implementation
- `alexandria-fabric` - [Fabric](https://fabricmc.net) implementation

```kotlin
repositories {
  mavenCentral()
  // for snapshot builds
  // maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
  implementation("io.github.aecsocket", "alexandria-MODULE", "VERSION")
}
```
