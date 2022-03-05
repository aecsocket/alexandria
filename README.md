<div align="center">

<a href="https://aecsocket.gitlab.io/minecommons"><img src="banner.png" width="1024" alt="Minecommons banner" /></a>

`1.4.2-SNAPSHOT`:
[![build](https://github.com/aecsocket/minecommons/actions/workflows/build.yml/badge.svg)](https://github.com/aecsocket/minecommons/actions/workflows/build.yml)

</div>

Common utilities for Minecraft-related projects, split into
implementations based on platform.

# Usage

## Packages

Using any package from the GitHub Packages registry requires you to
authorize with GitHub Packages.

### To create a token for yourself:

1. Visit https://github.com/settings/tokens/new
2. Create a token with only the `read:packages` scope
3. Save that token as an environment variable, `GPR_TOKEN`
4. Save your GitHub username as another environment variable, `GPR_ACTOR`

### To use a token in a workflow run:

Include the `github.actor` and `secrets.GITHUB_TOKEN` variables in the `env` block of your step:

```yml
- name: "Build"
  run: ./gradlew build
  env:
    GPR_ACTOR: "${{ github.actor }}"
    GPR_TOKEN: "${{ secrets.GITHUB_TOKEN }}"
```

### To use the token in your environment variable:

Use the `GPR_ACTOR`, `GPR_TOKEN` environment variables in your build scripts:

```kotlin
// authenticating with the repository
credentials {
    username = System.getenv("GPR_ACTOR")
    password = System.getenv("GPR_TOKEN")
}
```

**Note: Never include your token directly in your build scripts!**

Always use an environment variable (or similar).

<details>
<summary>Maven</summary>

### [How to authorize](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

#### In `~/.m2/settings.xml`

```xml
<servers>
  <server>
    <id>github-minecommons</id>
    <username>[username]</username>
    <password>[token]</password>
  </server>
</servers>
```

#### In `pom.xml`

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
            username = System.getenv("GPR_ACTOR")
            password = System.getenv("GPR_TOKEN")
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
