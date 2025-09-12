package dev.gonodono.glancet.tests

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets

internal const val ExtraProvider = "dev.gonodono.glancet.tests.extra.PROVIDER"

internal const val ItemCount = 50

internal const val ByOffset = "ByOffset"
internal const val DisplayedChild = "DisplayedChild"
internal const val ShowNext = "ShowNext"
internal const val ToPosition = "ToPosition"

internal inline val Context.appWidgetManager: AppWidgetManager
    get() = AppWidgetManager.getInstance(this)

internal fun View.applyInsetsListener() {
    if (Build.VERSION.SDK_INT < 35) return

    setOnApplyWindowInsetsListener { v, insets ->
        val bars = insets.getInsets(WindowInsets.Type.systemBars())
        (v.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            leftMargin = bars.left
            topMargin = bars.top
            rightMargin = bars.right
            bottomMargin = bars.bottom
        }
        insets
    }
}