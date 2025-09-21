package dev.gonodono.glimpse.scrollablelazy

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
 * The state interface for [ScrollableLazyColumn] and [ScrollableLazyVerticalGrid] that
 * exposes the smooth scroll functions for the underlying
 * [AdapterView][android.widget.AdapterView]s.
 *
 * **NB:** The smooth scroll functions have no effect on API levels 30 and
 * below. Glance performs only full updates, and the platform didn't start
 * preserving the relevant state across such updates until API level 31.
 */
public interface ScrollableLazyState {
    /**
     * Translates to a [RemoteViews.setInt][RemoteViews] call
     * for `smoothScrollToPosition`.
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun smoothScrollToPosition(position: Int)

    /**
     * Translates to a [RemoteViews.setInt][RemoteViews] call
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
 * Creates and remembers a [ScrollableLazyState] for use with [ScrollableLazyColumn] or
 * [ScrollableLazyVerticalGrid].
 */
@Composable
public fun rememberScrollableLazyState(): ScrollableLazyState =
    remember { ScrollableLazyState() }

internal fun ScrollableLazyState(): ScrollableLazyState =
    if (Build.VERSION.SDK_INT >= 31) {
        ScrollableLazyStateApi31()
    } else {
        ScrollableLazyStateApiMin()
    }

internal interface ScrollableLazyStateImpl : ScrollableLazyState {
    val action: ScrollableLazyAction?
}

@RequiresApi(31)
internal class ScrollableLazyStateApi31 : ScrollableLazyStateImpl {

    override var action
            by mutableStateOf<ScrollableLazyAction?>(null, neverEqualPolicy())
        private set

    private fun setAction(action: RemoteViews.(Int) -> Unit) {
        this.action = ScrollableLazyAction(action)
    }

    override fun smoothScrollToPosition(position: Int) {
        setAction { setInt(it, "smoothScrollToPosition", position) }
    }

    override fun smoothScrollByOffset(offset: Int) =
        setAction { setInt(it, "smoothScrollByOffset", offset) }
}

internal class ScrollableLazyStateApiMin : ScrollableLazyStateImpl {
    override val action: ScrollableLazyAction? = null
    override fun smoothScrollToPosition(position: Int) {}
    override fun smoothScrollByOffset(offset: Int) {}
}