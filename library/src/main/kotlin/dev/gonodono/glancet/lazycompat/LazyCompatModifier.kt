package dev.gonodono.glancet.lazycompat

import android.widget.RemoteViews
import androidx.glance.GlanceModifier
import dev.gonodono.glancet.find

internal fun GlanceModifier.lazyCompat(state: LazyCompatState): GlanceModifier =
    this then LazyCompatModifier(state as LazyCompatStateImpl)

internal class LazyCompatModifier(val state: LazyCompatStateImpl) :
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
@JvmName("applyLazyCompatIfPresent")
internal fun RemoteViews.applyLazyCompatIfPresent(
    modifier: GlanceModifier,
    adapterViewId: Int
) {
    val lazyCompat = modifier.find<LazyCompatModifier>() ?: return

    lazyCompat.state.action?.invoke(this, adapterViewId)
}