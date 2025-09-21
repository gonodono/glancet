# Module plugin

A Gradle plugin required for activation of the library's various features.

The plugin is applied and configured in the `build.gradle[.kts]` file for an
Android app or library. The following (`kts`) example shows all of the available
options along with their default values.

```kotlin
plugins {
    …
    id("io.github.gonodono.glimpse") version "…"
}

glimpse {
    remoteAdapter = true
    scrollableLazyColumn = true
    scrollableLazyVerticalGrid = true
    suppressPluginLogs = false
}

android {
    …
}

…
```

The feature flags are mainly meant for testing and debugging, but you may also
wish to disable features in order to avoid unnecessary bytecode modifications
for any that you're not using.