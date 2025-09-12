@file:Suppress("PackageDirectoryMismatch")

package dev.gonodono.glancet.lazycompat

import android.widget.RemoteViews
import androidx.glance.GlanceModifier

@Suppress("unused")
internal fun RemoteViews.applyLazyCompatIfPresent(
    modifier: GlanceModifier,
    appWidgetId: Int
) =
    this.setUsed()