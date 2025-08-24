package dev.gonodono.glancet.lazycompat

import android.os.Build
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Creates and remembers a [LazyCompatState] for use with [LazyColumnCompat] or
 * [LazyVerticalGridCompat].
 */
@Composable
public fun rememberLazyCompatState(): LazyCompatState =
    remember { LazyCompatStateImpl() }

/**
 * The state interface for [LazyColumnCompat] and [LazyVerticalGridCompat] that
 * exposes the smooth scroll functions for the underlying
 * [AdapterView][android.widget.AdapterView]s.
 *
 * **NB:** The smooth scroll functions have no effect on API levels 30 and
 * below. Glance performs only full updates, and the platform didn't start
 * preserving the relevant state across such updates until API level 31.
 */
public interface LazyCompatState {

    /**
     * Translates to a [RemoteViews.setInt][android.widget.RemoteViews] call
     * for `smoothScrollToPosition`.
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun smoothScrollToPosition(position: Int)

    /**
     * Translates to a [RemoteViews.setInt][android.widget.RemoteViews] call
     * for `smoothScrollByOffset`.
     *
     * This seems to be effective only with [offset] values of magnitude >= 2,
     * though that's not specific to this library. The same behavior can be
     * observed in a classic widget setup as well.
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun smoothScrollByOffset(offset: Int)
}

internal class LazyCompatStateImpl : LazyCompatState {

    var action by mutableStateOf<Action?>(null, neverEqualPolicy())

    override fun smoothScrollToPosition(position: Int) {
        if (Build.VERSION.SDK_INT < 31) return

        action = Action { setInt(it, "smoothScrollToPosition", position) }
    }

    override fun smoothScrollByOffset(offset: Int) {
        if (Build.VERSION.SDK_INT < 31) return

        action = Action { setInt(it, "smoothScrollByOffset", offset) }
    }
}

internal class Action(private val action: RemoteViews.(Int) -> Unit) :
        (RemoteViews, Int) -> Unit {

    private var wasInvoked = AtomicBoolean(false)

    override fun invoke(remoteViews: RemoteViews, adapterViewId: Int) {
        if (!wasInvoked.compareAndSet(false, true)) return
        remoteViews.action(adapterViewId)
    }
}