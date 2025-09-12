package dev.gonodono.glancet.lazycompat

import android.widget.RemoteViews
import java.util.concurrent.atomic.AtomicBoolean

internal class LazyCompatAction(private val action: RemoteViews.(Int) -> Unit) :
        (RemoteViews, Int) -> Unit {

    private val wasInvoked = AtomicBoolean(false)

    override fun invoke(remoteViews: RemoteViews, adapterViewId: Int) {
        if (!wasInvoked.compareAndSet(false, true)) return
        remoteViews.action(adapterViewId)
    }
}