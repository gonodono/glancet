# Glimpse

Tools and extended composables for [Glance app widgets][glance] on Android.

The current features comprise `AdapterView` workarounds and enhancements for
which Glance has no corresponding functionalities.

- **GlanceModifier.remoteAdapter**

  A modifier that allows the use of `AdapterView`s with `AndroidRemoteViews`,
  giving us access to `StackView` and `AdapterViewFlipper`.

- **ScrollableLazyColumn & ScrollableLazyVerticalGrid**

  Extended composables that offer access to `AdapterView`'s
  `smoothScrollToPosition` and `smoothScrollByOffset` methods, allowing
  programmatic scroll on API levels 31+ (which `remoteAdapter` also offers).

<br />

## Contents

- [**GlanceModifier.remoteAdapter**](#glancemodifierremoteadapter)
- [**ScrollableLazyColumn & ScrollableLazyVerticalGrid**](#scrollablelazycolumn--scrollablelazyverticalgrid)
- [**Gradle setup**](#gradle-setup)
- [**Version map**](#version-map)
- [**Project notes**](#project-notes)
- [**Documentation ↗**][documentation]

<br />

## GlanceModifier.remoteAdapter

`AdapterView`s don't work reliably in `AndroidRemoteViews` due to a bug in
`RemoteViews` that causes `setRemoteAdapter` to fail on nested instances in
certain host setups. This can be avoided by simply moving the `setRemoteAdapter`
call to the parent `RemoteViews`, but we don't have access to any of that in
Glance since it's all been abstracted away.

[`GlanceModifier.remoteAdapter`][remoteAdapter] works around this by passing the
adapter data to a function that's been injected into Glance's bytecode where the
parent `RemoteViews` can be accessed and modified directly.

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

    // The state holds the adapter data to apply later.
    // There are overloads for the RemoteCollectionItems versions too.
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

The state object is created with a standard `remember*State` function, one
available for each type of `AdapterView` (not counting overloads):

- [`rememberListViewState`][rememberListViewState]
- [`rememberGridViewState`][rememberGridViewState]
- [`rememberStackViewState`][rememberStackViewState]
- [`rememberAdapterViewFlipperState`][rememberAdapterViewFlipperState]

These functions are all separate for clarity and ease of use, though there are
really only two different types. The `ListView` and `GridView` ones return
exactly the same thing, as do the `StackView` and `AdapterViewFlipper` versions.
The difference between the two types is the scroll functions that each offers,
which are covered below.

Complete examples can be found in the `demo`'s
[`RemoteAdapterWidgets`][RemoteAdapterWidgets].

### Lint check

This feature comes with a lint check that will show an error if the modifier is
(part of) a direct argument to any composable that's not `AndroidRemoteViews`.
The check is quite simplistic at the moment. It will not notice, for example, if
you assign the modifier to a variable before passing it to an invalid
composable.

### Programmatic scroll

<sup>(API levels 31+)</sup>

The scroll functions work in basically the same manner as the adapter feature:
state data is attached to the modifier, whence it is later retrieved and applied
by a function injected into Glance's bytecode.

As mentioned above, `ListView` and `GridView` share a state type, and it defines
scroll functions that mirror those in `AbsListView`.

[`AbsListViewState`][AbsListViewState]:

```kotlin
fun smoothScrollToPosition(position: Int)
fun smoothScrollByOffset(offset: Int)
```

The state for `StackView` and `AdapterViewFlipper` defines functions from
`AdapterViewAnimator`.

[`AdapterViewAnimatorState`][AdapterViewAnimatorState]:

```kotlin
fun setDisplayedChild(whichChild: Int)
fun showNext()
fun showPrevious()
```

All of the scroll functions have the same API level restrictions: they only work
on API levels 31+. They are no-ops on prior versions, as noted in their docs.
This is due to the fact that Glance only ever performs full updates – i.e., no
`partiallyUpdateAppWidget` – and 31 is the first version to preserve state like
the scroll position across such updates.

Please note that this means that `AdapterViewFlipper` is essentially useless on
API levels < 31, since users cannot scroll it themselves.

Consult the [`ScrollableLazyColumnExample`](#example-1) below for a
straightforward demonstration of the scroll functions.

<br />

> [!WARNING]
> The programmatic scroll functions for both `remoteAdapter` and the
> `Scrollable` composables only work on API levels 31+, but there are currently
> no annotations or lint warnings to indicate that.

<br />

## ScrollableLazyColumn & ScrollableLazyVerticalGrid

<sup>(API levels 31+)</sup>
<br />

[`ScrollableLazyColumn`][ScrollableLazyColumn] and
[`ScrollableLazyVerticalGrid`][ScrollableLazyVerticalGrid] are wrappers around
their base composables that add a single state parameter which defines functions
from `AbsListView`, since they translate to `ListView`s and `GridView`s.

[`ScrollableLazyState`][ScrollableLazyState]:

```kotlin
fun smoothScrollToPosition(position: Int)
fun smoothScrollByOffset(offset: Int)
```

Both use the same [`rememberScrollableLazyState`][rememberScrollableLazyState]
function and state type.

Again these functions only work on API levels 31 and above. They are no-ops on
prior versions, as noted in their docs.

### Example

```kotlin
@Composable
fun ScrollableLazyColumnExample() {

  Column(modifier = GlanceModifier.fillMaxSize()) {

        // No setup or data for this; it's just exposing some command functions.
        val state = rememberScrollableLazyState()

        ScrollableLazyColumn(
            state = state,
            modifier = GlanceModifier
                .fillMaxWidth
                .defaultWeight()
        ) {
            items(…)
        }

        // It's a regular LazyColumn underneath so we can still use it as that.
        if (Build.VERSION.SDK_INT < 31) return@Column

        // 2 seems to be the minimum effective magnitude, but that's not unique
        // to this library. Classic RemoteViews setups display similar behavior.
        Button("-->", { state.smoothScrollByOffset(2) })
        Button("<--", { state.smoothScrollByOffset(-2) })
    }
}
```

Complete examples can be found in the `demo` module's
[`ScrollableLazyWidgets`][ScrollableLazyWidgets].

<br />

## Gradle setup

The library is accompanied by a custom Gradle plugin that handles the necessary
bytecode manipulation. This plugin is required for all of the library's current
features.

Everything is published to Maven Central, and an Android project should already
have that repository correctly specified in its `settings.gradle[.kts]`. If not,
consult [this project's settings file][settings] for the proper configuration.

Note that the library and plugin are published using the hosting service's
domain, so their group ID – `io.github.gonodono.glimpse` – is different than the
Java package name used in code, `dev.gonodono.glimpse`. The `io.github`
identifiers are only ever used for build dependencies.

The following snippets are simply examples. Your specific setup may be different.

`libs.versions.toml`:

```toml
[versions]
#…
glimpse = "?.?.?*"

[libraries]
#…
glimpse-library = { module = "io.github.gonodono.glimpse:glimpse", version.ref = "glimpse" }

[plugins]
#…
glimpse-plugin = { id = "io.github.gonodono.glimpse", version.ref = "glimpse" }
```

<sup>* Check [Releases][releases] for the latest.</sup>

Project's `build.gradle.kts`:

```kotlin
plugins {
    …
    alias(libs.plugins.glimpse.plugin) apply false
}
```

App's `build.gradle.kts`:

```kotlin
plugins {
    …
    alias(libs.plugins.glimpse.plugin)
}

…

glimpse {
    suppressPluginLogs = true
}

…

dependencies {
    …
    implementation(libs.glimpse.library)
}
```

The short `glimpse {}` example above shows how to disable the logs, and there's
a full example with all available options and their default values shown in [the
plugin's docs][plugin-docs].

### Plugin logs

For reference, the default settings will produce the following build logs for a
debug variant. (A single log would probably be preferable, but the particular
tool involved makes that a bit tricky.)

```
Glimpse has modified debug to enable remoteAdapter.
Glimpse has modified debug to enable scrollableLazyColumn.
Glimpse has modified debug to enable scrollableLazyVerticalGrid.
```

### Lint check

A lint check is included that will show errors if the plugin is missing, or if a
given feature has been inadvertently disabled while trying to use it. This check
relies on build output from the plugin. It will not work correctly if the build
isn't up to date with the plugin settings.

> [!IMPORTANT]
> Be sure to rebuild your project after adding the plugin or changing any of its
> options.

<br />

## Version map

|    Glance     | Glimpse | Plugin |
|:-------------:|:-------:|:------:|
|     1.1.1     |  0.0.1  | 0.0.1  |
| 1.2.0-alpha01 |    "    |   "    |
| 1.2.0-beta01  |    "    |   "    |

This first release probably works with some older Glance versions too, but I've
not tested any.

> [!WARNING]
> Currently, these mappings are not enforced in any way. It is up to the user to
> ensure correct corresponding versions. There is no guaranteed behavior for
> mismatches; it might work perfectly, or it may fail during build or runtime,
> it might throw an explicit Exception or it could fail silently and cause
> delayed issues, etc.

<br />

## Project notes

- This project used to be called Glancet, but I abruptly changed it before
  publishing the first release. In addition to the relevant package and build
  changes, I've also renamed the composables. If you were using this already,
  my apologies.

- The library accomplishes its effects by slightly altering Glance's bytecode to
  insert calls to local code where the underlying `RemoteViews` are modified
  directly. The bytecode manipulation is performed by the custom Gradle plugin
  using [a built-in AGP functionality][transform] along with some basic Java
  ASM.

- All of the current features involve custom `GlanceModifier` implementations,
  and Glance prints a warning log anytime it encounters one that's not its own.
  For example, in a build without obfuscation:

  ```
  Unknown modifier 'dev.gonodono.glimpse.remoteadapter.RemoteAdapterModifier@bc48ce6', nothing done.
  Unknown modifier 'dev.gonodono.glimpse.scrollablelazy.ScrollableLazyModifier@f751447', nothing done.
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
  navigation buttons in the rest of the widgets, are all disabled due to the
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

[glance]: https://developer.android.com/develop/ui/compose/glance
[documentation]: https://gonodono.github.io/glimpse
[remoteAdapter]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/remote-adapter.html
[rememberListViewState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/remember-list-view-state.html
[rememberGridViewState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/remember-grid-view-state.html
[rememberStackViewState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/remember-stack-view-state.html
[rememberAdapterViewFlipperState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/remember-adapter-view-flipper-state.html
[RemoteAdapterWidgets]: demo/src/main/kotlin/dev/gonodono/glimpse/demo/RemoteAdapterWidgets.kt
[AbsListViewState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/-abs-list-view-state/index.html
[AdapterViewAnimatorState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.remoteadapter/-adapter-view-animator-state/index.html
[ScrollableLazyColumn]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.scrollablelazy/-scrollable-lazy-column.html
[ScrollableLazyVerticalGrid]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.scrollablelazy/-scrollable-lazy-vertical-grid.html
[ScrollableLazyState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.scrollablelazy/-scrollable-lazy-state/index.html
[rememberScrollableLazyState]: https://gonodono.github.io/glimpse/glimpse/library/dev.gonodono.glimpse.scrollablelazy/remember-scrollable-lazy-state.html
[ScrollableLazyWidgets]: demo/src/main/kotlin/dev/gonodono/glimpse/demo/ScrollableLazyWidgets.kt
[settings]: settings.gradle.kts
[releases]: https://github.com/gonodono/glimpse/releases
[plugin-docs]: https://gonodono.github.io/glimpse/plugin/index.html
[transform]: https://developer.android.com/build/releases/gradle-plugin-api-updates#support_for_transforming_bytecode
[issues]: https://github.com/gonodono/glimpse/issues