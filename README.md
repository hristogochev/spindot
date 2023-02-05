# DotLoaders

### Notice

This library is a heavily modified fork of
[DotLoadersPack-Android](https://github.com/agrawalsuneet/DotLoadersPack-Android).</br>
All modified files have been explicitly marked as such.</br>
For the original Apache-2.0 license please
view [LICENSE-original](https://github.com/hristogochev/DotLoaders/blob/main/LICENSE-original).</br>
All code except that required to implement the logic of each animation has been rewritten.</br>

### Usage

To use this library, add jitpack to your project's repositories:

```groovy
maven { url "https://jitpack.io" }
```

And then add the following dependency to your build.gradle file:

```groovy
implementation 'com.github.hristogochev:dotloaders:1.0.0'
```

### Features

* 12 different dot-loaders for your needs.
* Full compatibility with Jetpack Compose.
* Full compatibility with XML.

### Differences with the original

* Only available for API 21 and forward.
* Each loader is now available for Jetpack Compose.
* Additional optimizations have been applied resulting in smoother experience.
* You can now disable the automatic playing of animations and control it manually.
* Animations only play while visible, otherwise they automatically stop to save resources.
* Project has been updated to the newest Kotlin and Compose versions.
* Code has been made more maintainable by removing tight coupling and extracting common logic.
* Each loader can now be modified separately without breaking the others.
