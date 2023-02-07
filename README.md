# DotLoaders

### Notice

This library is a heavily modified fork of
[DotLoadersPack-Android](https://github.com/agrawalsuneet/DotLoadersPack-Android).</br>

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

### BounceLoader

<details>
  <summary>Implementations</summary>

#### Compose
```kotlin
BounceLoader(
    ballRadius = 30.dp,
    ballColor = Color.Magenta,
    showShadow = true,
    shadowColor = Color.LightGray,
    animDuration = 1200
)
```

#### XML
```xml

<com.hristogochev.dotloaders.loaders.BounceLoader 
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:bounce_ballRadius="30dp"
    app:bounce_ballcolor="@color/magenta"
    app:bounce_showShadow="true"
    app:bounce_shadowColor="@color/light_gray"
    app:bounce_animDuration="1200" />
```

</details>

