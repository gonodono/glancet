package dev.gonodono.glancet.lazycompat

import android.annotation.SuppressLint
import android.widget.RemoteViews
import androidx.glance.Emittable
import androidx.glance.GlanceModifier
import androidx.glance.findModifier

internal fun GlanceModifier.lazyCompat(state: LazyCompatState): GlanceModifier =
    this then LazyCompatModifier(state as LazyCompatStateImpl)

private class LazyCompatModifier(val state: LazyCompatStateImpl) :
    GlanceModifier.Element {

    init {
        // The actual read for this occurs during RemoteViews construction,
        // which apparently happens outside of measure/layout/draw, so we
        // need to read it ourselves in order to trigger recomposition.
        state.action
    }
}

// NB: This must remain in the same file as the corresponding GlanceModifier
// extension function in order to ensure that Proguard/R8 handles it correctly.
@SuppressLint("RestrictedApi")
@JvmName("applyLazyCompatIfPresent")
internal fun RemoteViews.applyLazyCompatIfPresent(
    element: Emittable,
    adapterViewId: Int
) {
    val modifier = element.modifier.findModifier<LazyCompatModifier>() ?: return

    modifier.state.action?.invoke(this, adapterViewId)
}