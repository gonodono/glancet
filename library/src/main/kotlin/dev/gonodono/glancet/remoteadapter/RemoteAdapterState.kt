package dev.gonodono.glancet.remoteadapter

import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue

/**
 * Parent interface for the specific [remoteAdapter] states.
 */
public interface RemoteAdapterState

internal interface RemoteAdapterStateImpl : RemoteAdapterState {
    val adapter: RemoteAdapter
    val action: RemoteAdapterAction?
}

@RequiresApi(31)
internal open class RemoteAdapterStateApi31(override val adapter: RemoteAdapter) :
    RemoteAdapterStateImpl {

    override var action
            by mutableStateOf<RemoteAdapterAction?>(null, neverEqualPolicy())
        private set

    protected fun setAction(action: RemoteViews.(Int) -> Unit) {
        this.action = RemoteAdapterAction(adapter.adapterViewId, action)
    }
}

internal open class RemoteAdapterStateApiMin(override val adapter: RemoteAdapter) :
    RemoteAdapterStateImpl {

    override val action: RemoteAdapterAction? = null
}