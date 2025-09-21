@file:Suppress("PackageDirectoryMismatch")

package dev.gonodono.glimpse.scrollablelazy

import android.widget.RemoteViews
import androidx.glance.GlanceModifier

@Suppress("unused")
internal fun RemoteViews.applyScrollableLazyIfPresent(
    modifier: GlanceModifier,
    appWidgetId: Int
) =
    this.setUsed()