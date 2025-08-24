# Glancet

Tools for Glance app widgets on Android

<br />

**Current Features:**

- `GlanceModifier.remoteAdapter`

  Allows the use of `AdapterView`s with `AndroidRemoteViews`, giving us access
  to `StackView` and `AdapterViewFlipper`, analogs for which Glance doesn't yet
  have.

- `LazyColumnCompat` & `LazyVerticalGridCompat`

  These extended composables add a state parameter that gives access to
  `AdapterView`'s `smoothScrollToPosition` and `smoothScrollByOffset` methods,
  allowing programmatic scroll on API levels 31+ (which `remoteAdapter` also
  offers).

<br />

## Contents

- [**Setup**](#setup)
- [**GlanceModifier.remoteAdapter**](#glancemodifierremoteadapter)
- [**LazyColumnCompat & LazyVerticalGridCompat**](#lazycolumncompat--lazyverticalgridcompat)
- [**Version map**](#version-map)
- [**Notes**](#notes)
- [**Documentation ↗**][documentation]

<br />

## Setup

The library accomplishes its effects by slightly altering Glance's bytecode to
insert calls to local code where the underlying `RemoteViews` are modified
directly. The bytecode manipulation is handled by a custom Gradle plugin that
uses [a built-in AGP functionality][transform], along with some basic Java ASM.

### Library publishing

Until everything gets published to the main repository services, the `library`
and `plugin` modules will have to be built and published manually in order to be
able to use this (unless you want to include the projects directly). If you
would want to utilize your local Maven repository, all it takes is to run two
predefined Gradle tasks in this project, and then to add a couple of
`mavenLocal()` lines to your app's project.

1. In Glancet, run `./gradlew :library:publishToMavenLocal` either through a
   terminal, or from the Gradle task window in Android Studio. (Or, if you're
   reading this in your IDE and that command renders as an action, just run it
   from here.)

2. In Glancet again, run `./gradlew :plugin:publishToMavenLocal` the same way.

3. In your app project's `settings.gradle.kts`, assuming a modern setup, add
   `mavenLocal()` to the `repositories {}` blocks that are inside both the
   `pluginManagement {}` block, and the `dependencyResolutionManagement {}`
   block. For example:

   ```kotlin
   pluginManagement {
       repositories {
           …
           mavenLocal()
       }
   }
   dependencyResolutionManagement {
       …
       repositories {
           …
           mavenLocal()
       }
   }
   …
   ```

That's it. The repository, which is just a directory under your user account's
home, will be set up automagically, if it's not already there. After that, this
is a standard plugin and dependency setup, which these days looks something like
the following.

### App configuration

`libs.versions.toml`:

```toml
[versions]
#…
glancet = "0.0.1"

[libraries]
#…
glancet-library = { module = "dev.gonodono.glancet:glancet", version.ref = "glancet" }

[plugins]
#…
glancet-plugin = { id = "dev.gonodono.glancet", version.ref = "glancet" }
```

Project's `build.gradle.kts`:

```kotlin
plugins {
    …
    alias(libs.plugins.glancet.plugin) apply false
}
```

App's `build.gradle.kts`:

```kotlin
plugins {
    …
    alias(libs.plugins.glancet.plugin)
}

…

glancet {
    suppressPluginLogs = true
}

…

dependencies {
    …
    implementation(libs.glancet.library)
}
```

The short `glancet {}` example above shows how to disable the logs, and there's
a full example with all available options and their default values in the `demo`
modules's [`build.gradle.kts` file][demo-build].

### Plugin logs

For reference, the default settings will produce the following logs for a debug
variant. (A single log would probably be preferable, but this particular tool
makes that a bit tricky.)

```
Glancet has modified debug to enable remoteAdapter.
Glancet has modified debug to enable lazyColumnCompat.
Glancet has modified debug to enable lazyVerticalGridCompat.
```

### Lint check

All of the library's current features require the plugin, and there is a lint
check that will show errors if the plugin is missing, or if a given feature has
been inadvertently disabled while trying to use it. This check relies on build
output from the plugin. It will not work correctly if the build isn't up to date
with the plugin settings.

<br />

> [!IMPORTANT]
> Be sure to rebuild your project after adding the plugin or changing any of its
> options.

<br />

## GlanceModifier.remoteAdapter

There's apparently a bug in `RemoteViews` that causes certain operations to
fail on nested instances that are attached with `addView`/`addStableView` if
those operations are performed directly on the nested ones. `setRemoteAdapter`
is one of those that will fail in such a setup, and though simply moving the
relevant operation to the parent will fix it, there's no way to do that with
Glance's public API.

The manner in which Glance pieces together its own composables manages to avoid
this issue. When we use `AndroidRemoteViews`, however, we have to pass the whole
shebang already assembled, since we have no access to the parent `RemoteViews`,
as that's all been abstracted away. `remoteAdapter` basically passes the adapter
data to an injected function where we can attach it to the parent instead.

### Example

The sole difference here from a regular setup is that instead of calling
`setRemoteAdapter` on the `RemoteViews`, a state object is created with the
adapter data and then attached to the modifier chain to be applied later by the
injected library function.

```kotlin
@Composable
fun RemoteAdapterExample() {

    // Don't call setRemoteAdapter on this RemoteViews.
    val stackViewViews: RemoteViews = …

    // Nothing special with this, a regular Intent for a RemoteViewsService.
    val serviceIntent: Intent = …

    // The state holds the Adapter data to apply later.
    // There's also an overload for the RemoteCollectionItems version of
    // setRemoteAdapter, as well as one that works with compat items.
    val state = rememberStackViewState(R.id.stack_view, serviceIntent)

    // Order doesn't matter for the remoteAdapter call.
    AndroidRemoteViews(
        remoteViews = stackViewViews,
        modifier = GlanceModifier
            .remoteAdapter(state)
            .fillMaxSize()
    )
}
```

You can see that the state object is created with a standard `remember*State`
function, one available for each type of `AdapterView` (not counting overloads):

- `rememberListViewState`
- `rememberGridViewState`
- `rememberStackViewState`
- `rememberAdapterViewFlipperState`

The separate functions are for clarity and ease of use, though there are really
only two different types. The `ListView` and `GridView` ones return exactly the
same thing, as do the `StackView` and `AdapterViewFlipper` versions. The
difference between the two types is the scroll functions that each offers, which
are covered below.

Complete examples can be found in the demo's [`RemoteAdapterWidgets`
file][remote-adapter].

### Lint check

This feature comes with a lint check that will show an error if the modifier is
(part of) a direct argument to any composable that's not `AndroidRemoteViews`.
The check is quite simplistic at the moment. It will not notice, for example, if
you assign the modifier to a variable before passing it to the wrong composable.

### Programmatic scroll

As mentioned, `remoteAdapter` states also provide access to the smooth scroll
functions, which are used by `ListView` and `GridView`. `StackView` and
`AdapterViewFlipper` are actually a different type of `AdapterView`, and their
state interface offers `setDisplayedChild`, `showNext`, and `showPrevious`
instead.

Both types have the same API restrictions as described in the next section.
Please note that those restrictions mean that `AdapterViewFlipper` is
essentially useless on API levels < 31, since the user can't scroll it
themselves.

<br />

> [!WARNING]
> The programmatic scroll functions only work on API levels 31+, but there are
> currently no annotations or warnings to indicate that.

<br />

## LazyColumnCompat & LazyVerticalGridCompat

Each of these simply adds to the base function a single parameter for a "state"
interface that exposes the `smoothScrollToPosition` and `smoothScrollByOffset`
functions.

### API restrictions

Unfortunately these functions only work on API levels 31 and above, due to the
fact that Glance only ever performs full updates – i.e., no
`partiallyUpdateAppWidget` – and 31 is the first version to preserve state like
scroll position and displayed child across such updates.

I'm not sure how to handle this, however, since `@RequiresApi` annotations are
a bit unorthodox in Compose. It seems they prefer to mention in the docs that a
given feature doesn't work on certain versions, so that's how it's setup for
this first release: the scroll and child functions simply don't do anything on
API levels prior to 31, and it's noted in the relevant docs. I may add some lint
checks in the future, or it may end up with some `@RequiresApi`s.

### Example

```kotlin
@Composable
fun LazyColumnCompatExample() {

  Column(modifier = GlanceModifier.fillMaxSize()) {

      // No setup or data for this; it's just exposing some command functions.
      val state = rememberLazyCompatState()

        LazyColumnCompat(
            state = state,
            modifier = GlanceModifier
                .fillMaxWidth
                .defaultWeight()
        ) {
            items(…)
        }

        // It's a regular LazyColumn underneath so we can still use it as that.
        if (Build.VERSION.SDK_INT < 31) return@Column

        // 2 seems to be the minimum effective absolute value, but that's not
        // specific to this library. Regular RemoteViews setups behave similarly.
        Button("-->", { state.smoothScrollByOffset(2) })
        Button("<--", { state.smoothScrollByOffset(-2) })
    }
}
```

Both of the compat composables use the same `rememberLazyCompatState` function
and state type.

Complete examples can be found in the demo's [`LazyCompatWidgets`
file][lazy-compat].

<br />

## Version map

This first release probably works with some older Glance versions too, but I've
not tested any.

|    Glance     | Glancet | Plugin |
|:-------------:|:-------:|:------:|
|     1.1.1     |  0.0.1  | 0.0.1  |
| 1.2.0-alpha01 |    "    |   "    |

<br />

## Notes

- All of the current features involve custom `GlanceModifier` implementations,
  and Glance prints a warning log anytime it encounters one that's not its own.
  For example, in a build without obfuscation:

  ```
  Unknown modifier 'dev.gonodono.glancet.lazycompat.LazyCompatModifier@f751447', nothing done.
  Unknown modifier 'dev.gonodono.glancet.remoteadapter.RemoteAdapterModifier@bc48ce6', nothing done.
  ```

  These might be surgically removed or corrected in a future version of this
  library, but I've left them in place for now. They're handy for debugging, and
  also as a little extra assurance that the bytecode changes didn't go horribly
  wrong.

- The `demo` module contains an app with various simple widgets that demonstrate
  everything available in the library.

  <!--suppress HtmlDeprecatedAttribute -->
  <p align="center">
  <!--suppress CheckImageSize -->
  <img src="images/screenshot.png" 
  alt="A screenshot of the four different AdapterView widgets." 
  width="30%" />
  </p>

  On API levels 30 and below, the `AdapterViewFlipper` widget, as well as the
  arrow buttons in the rest of the widgets, are all disabled due to the
  aforementioned restrictions.

  The Activity's UI (not shown) was designed on and for medium phone setups
  only, so it might not look that great on other configurations. Just a heads
  up.

- GitHub's Issues feature is enabled for this repo. Please report any bugs or
  glitches [there][issues].

<br />

<br />

## License

MIT License

Copyright (c) 2025 Mike M.

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

[documentation]: https://gonodono.github.io/glancet
[transform]: https://developer.android.com/build/releases/gradle-plugin-api-updates#support_for_transforming_bytecode
[demo-build]: demo/build.gradle.kts
[remote-adapter]: demo/src/main/kotlin/dev/gonodono/glancet/demo/RemoteAdapterWidgets.kt
[lazy-compat]: demo/src/main/kotlin/dev/gonodono/glancet/demo/LazyCompatWidgets.kt
[issues]: https://github.com/gonodono/glancet/issues