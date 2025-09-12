package dev.gonodono.glancet.lazycompat

import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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

/**
 * Creates and remembers a [LazyCompatState] for use with [LazyColumnCompat] or
 * [LazyVerticalGridCompat].
 */
@Composable
public fun rememberLazyCompatState(): LazyCompatState =
    remember { LazyCompatState() }

internal fun LazyCompatState(): LazyCompatState =
    if (Build.VERSION.SDK_INT >= 31) {
        LazyCompatStateApi31()
    } else {
        LazyCompatStateApiMin()
    }

internal interface LazyCompatStateImpl : LazyCompatState {
    val action: LazyCompatAction?
}

@RequiresApi(31)
internal class LazyCompatStateApi31 : LazyCompatStateImpl {

    override var action
            by mutableStateOf<LazyCompatAction?>(null, neverEqualPolicy())
        private set

    private fun setAction(action: RemoteViews.(Int) -> Unit) {
        this.action = LazyCompatAction(action)
    }

    override fun smoothScrollToPosition(position: Int) {
        setAction { setInt(it, "smoothScrollToPosition", position) }
    }

    override fun smoothScrollByOffset(offset: Int) =
        setAction { setInt(it, "smoothScrollByOffset", offset) }
}

internal class LazyCompatStateApiMin : LazyCompatStateImpl {
    override val action: LazyCompatAction? = null
    override fun smoothScrollToPosition(position: Int) {}
    override fun smoothScrollByOffset(offset: Int) {}
}