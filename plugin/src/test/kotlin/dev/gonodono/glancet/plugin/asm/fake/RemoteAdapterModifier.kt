@file:Suppress("PackageDirectoryMismatch")

package dev.gonodono.glancet.remoteadapter

import android.widget.RemoteViews
import androidx.glance.GlanceModifier

@Suppress("unused")
internal fun RemoteViews.setRemoteAdapterIfPresent(modifier: GlanceModifier) =
    this.setUsed()