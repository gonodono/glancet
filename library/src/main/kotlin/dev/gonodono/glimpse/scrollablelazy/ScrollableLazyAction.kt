package dev.gonodono.glimpse.scrollablelazy

import android.widget.RemoteViews
import java.util.concurrent.atomic.AtomicBoolean

internal class ScrollableLazyAction(private val action: RemoteViews.(Int) -> Unit) :
        (RemoteViews, Int) -> Unit {

    private val wasInvoked = AtomicBoolean(false)

    override fun invoke(remoteViews: RemoteViews, adapterViewId: Int) {
        if (!wasInvoked.compareAndSet(false, true)) return
        remoteViews.action(adapterViewId)
    }
}