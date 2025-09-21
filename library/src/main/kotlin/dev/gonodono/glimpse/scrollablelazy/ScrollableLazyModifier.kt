package dev.gonodono.glimpse.scrollablelazy

import android.widget.RemoteViews
import androidx.glance.GlanceModifier
import dev.gonodono.glimpse.find

internal fun GlanceModifier.scrollableLazy(state: ScrollableLazyState): GlanceModifier =
    this then ScrollableLazyModifier(state as ScrollableLazyStateImpl)

internal class ScrollableLazyModifier(val state: ScrollableLazyStateImpl) :
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
@JvmName("applyScrollableLazyIfPresent")
internal fun RemoteViews.applyScrollableLazyIfPresent(
    modifier: GlanceModifier,
    adapterViewId: Int
) {
    val scrollableLazy = modifier.find<ScrollableLazyModifier>() ?: return

    scrollableLazy.state.action?.invoke(this, adapterViewId)
}