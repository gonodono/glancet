package dev.gonodono.glimpse.remoteadapter

import android.widget.RemoteViews
import java.util.concurrent.atomic.AtomicBoolean

internal class RemoteAdapterAction(
    private val adapterViewId: Int,
    private val action: RemoteViews.(Int) -> Unit
) : (RemoteViews) -> Unit {

    private val wasInvoked = AtomicBoolean(false)

    override fun invoke(remoteViews: RemoteViews) {
        if (!wasInvoked.compareAndSet(false, true)) return
        remoteViews.action(adapterViewId)
    }
}