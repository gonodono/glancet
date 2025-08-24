package dev.gonodono.glancet.lazycompat

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.layout.Alignment

/**
 * A wrapper around [LazyColumn] that injects the library's custom state
 * exposing the smooth scroll functions for the underlying
 * [AdapterView][android.widget.AdapterView]s.
 *
 * **NB:** The smooth scroll functions have no effect on API levels 30 and
 * below. Glance performs only full updates, and the platform didn't start
 * preserving the relevant state across such updates until API level 31.
 */
@Composable
public fun LazyColumnCompat(
    state: LazyCompatState,
    modifier: GlanceModifier = GlanceModifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit
): Unit =
    LazyColumn(
        modifier = modifier.lazyCompat(state),
        horizontalAlignment = horizontalAlignment,
        content = content
    )

/**
 * A wrapper around [LazyColumn] that injects the library's custom state
 * exposing the smooth scroll functions for the underlying
 * [AdapterView][android.widget.AdapterView]s.
 *
 * **NB:** The smooth scroll functions have no effect on API levels 30 and
 * below. Glance performs only full updates, and the platform didn't start
 * preserving the relevant state across such updates until API level 31.
 */
@ExperimentalGlanceApi
@Composable
public fun LazyColumnCompat(
    state: LazyCompatState,
    activityOptions: Bundle,
    modifier: GlanceModifier = GlanceModifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit
): Unit =
    LazyColumn(
        activityOptions = activityOptions,
        modifier = modifier.lazyCompat(state),
        horizontalAlignment = horizontalAlignment,
        content = content
    )