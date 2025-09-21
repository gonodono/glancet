@file:Suppress("PackageDirectoryMismatch")

package dev.gonodono.glimpse.remoteadapter

import android.widget.RemoteViews
import androidx.glance.GlanceModifier

@Suppress("unused")
internal fun RemoteViews.setRemoteAdapterIfPresent(modifier: GlanceModifier) =
    this.setUsed()