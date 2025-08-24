package dev.gonodono.glancet.remoteadapter

import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.widget.RemoteViewsCompat
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import java.util.concurrent.atomic.AtomicBoolean

// ListView

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [ListView][android.widget.ListView] populated with
 * [RemoteViewsCompat.RemoteCollectionItems].
 */
@Composable
public fun rememberListViewState(
    @IdRes listViewId: Int,
    items: RemoteViewsCompat.RemoteCollectionItems
): AbsListViewState {
    val context = LocalContext.current
    val glanceId = LocalGlanceId.current
    val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)

    return remember(listViewId, context, appWidgetId, items) {
        val adapter =
            RemoteAdapter.Items.Compat(
                adapterViewId = listViewId,
                context = context,
                appWidgetId = appWidgetId,
                items = items
            )
        AbsListViewStateImpl(adapter)
    }
}

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [ListView][android.widget.ListView] populated with
 * [RemoteViews.RemoteCollectionItems].
 */
@RequiresApi(31)
@Composable
public fun rememberListViewState(
    @IdRes listViewId: Int,
    items: RemoteViews.RemoteCollectionItems
): AbsListViewState =
    remember(listViewId, items) {
        val adapter = RemoteAdapter.Items.Platform(listViewId, items)
        AbsListViewStateImpl(adapter)
    }

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [ListView][android.widget.ListView] populated with a [RemoteViewsService].
 */
@Composable
public fun rememberListViewState(
    @IdRes listViewId: Int,
    intent: Intent
): AbsListViewState =
    remember(listViewId, intent) {
        val adapter = RemoteAdapter.Intents(listViewId, intent)
        AbsListViewStateImpl(adapter)
    }

// GridView

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [GridView][android.widget.GridView] populated with
 * [RemoteViewsCompat.RemoteCollectionItems].
 */
@Composable
public fun rememberGridViewState(
    @IdRes gridViewId: Int,
    items: RemoteViewsCompat.RemoteCollectionItems
): AbsListViewState =
    rememberListViewState(gridViewId, items)

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [GridView][android.widget.GridView] populated with
 * [RemoteViews.RemoteCollectionItems].
 */
@RequiresApi(31)
@Composable
public fun rememberGridViewState(
    @IdRes gridViewId: Int,
    items: RemoteViews.RemoteCollectionItems
): AbsListViewState =
    rememberListViewState(gridViewId, items)

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [GridView][android.widget.GridView] populated with a [RemoteViewsService].
 */
@Composable
public fun rememberGridViewState(
    @IdRes gridViewId: Int,
    intent: Intent
): AbsListViewState =
    rememberListViewState(gridViewId, intent)

// StackView

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [StackView][android.widget.StackView] populated with
 * [RemoteViewsCompat.RemoteCollectionItems].
 */
@Composable
public fun rememberStackViewState(
    @IdRes stackViewId: Int,
    items: RemoteViewsCompat.RemoteCollectionItems
): AdapterViewAnimatorState {
    val context = LocalContext.current
    val glanceId = LocalGlanceId.current
    val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)

    return remember(stackViewId, context, appWidgetId, items) {
        val adapter =
            RemoteAdapter.Items.Compat(
                adapterViewId = stackViewId,
                context = context,
                appWidgetId = appWidgetId,
                items = items
            )
        AdapterViewAnimatorStateImpl(adapter)
    }
}

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [StackView][android.widget.StackView] populated with
 * [RemoteViews.RemoteCollectionItems].
 */
@RequiresApi(31)
@Composable
public fun rememberStackViewState(
    @IdRes stackViewId: Int,
    items: RemoteViews.RemoteCollectionItems
): AdapterViewAnimatorState =
    remember(stackViewId, items) {
        val adapter = RemoteAdapter.Items.Platform(stackViewId, items)
        AdapterViewAnimatorStateImpl(adapter)
    }

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [StackView][android.widget.StackView] populated with a [RemoteViewsService].
 */
@Composable
public fun rememberStackViewState(
    @IdRes stackViewId: Int,
    intent: Intent
): AdapterViewAnimatorState =
    remember(stackViewId, intent) {
        val adapter = RemoteAdapter.Intents(stackViewId, intent)
        AdapterViewAnimatorStateImpl(adapter)
    }

// AdapterViewFlipper

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [AdapterViewFlipper][android.widget.AdapterViewFlipper] populated with
 * [RemoteViewsCompat.RemoteCollectionItems].
 */
@Composable
public fun rememberAdapterViewFlipperState(
    @IdRes adapterViewFlipperId: Int,
    items: RemoteViewsCompat.RemoteCollectionItems
): AdapterViewAnimatorState =
    rememberStackViewState(adapterViewFlipperId, items)

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [AdapterViewFlipper][android.widget.AdapterViewFlipper] populated with
 * [RemoteViews.RemoteCollectionItems].
 */
@RequiresApi(31)
@Composable
public fun rememberAdapterViewFlipperState(
    @IdRes adapterViewFlipperId: Int,
    items: RemoteViews.RemoteCollectionItems
): AdapterViewAnimatorState =
    rememberStackViewState(adapterViewFlipperId, items)

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [AdapterViewFlipper][android.widget.AdapterViewFlipper] populated with a
 * [RemoteViewsService].
 */
@Composable
public fun rememberAdapterViewFlipperState(
    @IdRes adapterViewFlipperId: Int,
    intent: Intent
): AdapterViewAnimatorState =
    rememberStackViewState(adapterViewFlipperId, intent)

/**
 * Parent interface for the specific [remoteAdapter] states.
 */
public interface RemoteAdapterState

/**
 * The state interface for [ListView][android.widget.ListView] and
 * [GridView][android.widget.GridView].
 */
public interface AbsListViewState : RemoteAdapterState {

    /**
     * Translates to a [RemoteViews.setInt] call for `smoothScrollToPosition`.
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun smoothScrollToPosition(position: Int)

    /**
     * Translates to a [RemoteViews.setInt] call for `smoothScrollByOffset`.
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
 * The state interface for [StackView][android.widget.StackView] and
 * [AdapterViewFlipper][android.widget.AdapterViewFlipper].
 */
public interface AdapterViewAnimatorState : RemoteAdapterState {

    /**
     * Translates to [RemoteViews.showNext].
     *
     * The corresponding [RemoteViews] method has been deprecated. They
     * recommend using [setDisplayedChild][RemoteViews.setDisplayedChild]
     * instead, but that doesn't work with [StackView][android.widget.StackView]
     * since the user can scroll it.
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun showNext()

    /**
     * Translates to [RemoteViews.showPrevious].
     *
     * The corresponding [RemoteViews] method has been deprecated. They
     * recommend using [setDisplayedChild][RemoteViews.setDisplayedChild]
     * instead, but that doesn't work with [StackView][android.widget.StackView]
     * since the user can scroll it.
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun showPrevious()

    /**
     * Translates to [RemoteViews.setDisplayedChild].
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun setDisplayedChild(whichChild: Int)
}

internal open class RemoteAdapterStateImpl(val adapter: RemoteAdapter) :
    RemoteAdapterState {

    var action by mutableStateOf<Action?>(null, neverEqualPolicy())
}

private class AbsListViewStateImpl(adapter: RemoteAdapter) :
    RemoteAdapterStateImpl(adapter),
    AbsListViewState {

    override fun smoothScrollToPosition(position: Int) {
        if (Build.VERSION.SDK_INT < 31) return

        val viewId = adapter.adapterViewId
        action = Action { setInt(viewId, "smoothScrollToPosition", position) }
    }

    override fun smoothScrollByOffset(offset: Int) {
        if (Build.VERSION.SDK_INT < 31) return

        val viewId = adapter.adapterViewId
        action = Action { setInt(viewId, "smoothScrollByOffset", offset) }
    }
}

private class AdapterViewAnimatorStateImpl(adapter: RemoteAdapter) :
    RemoteAdapterStateImpl(adapter),
    AdapterViewAnimatorState {

    override fun showNext() {
        if (Build.VERSION.SDK_INT < 31) return

        val viewId = adapter.adapterViewId
        action = Action { @Suppress("DEPRECATION") showNext(viewId) }
    }

    override fun showPrevious() {
        if (Build.VERSION.SDK_INT < 31) return

        val viewId = adapter.adapterViewId
        action = Action { @Suppress("DEPRECATION") showPrevious(viewId) }
    }

    override fun setDisplayedChild(whichChild: Int) {
        if (Build.VERSION.SDK_INT < 31) return

        val viewId = adapter.adapterViewId
        action = Action { setDisplayedChild(viewId, whichChild) }
    }
}

internal class Action(private val action: RemoteViews.() -> Unit) :
        (RemoteViews) -> Unit {

    private var wasInvoked = AtomicBoolean()

    override fun invoke(remoteViews: RemoteViews) {
        if (!wasInvoked.compareAndSet(false, true)) return
        remoteViews.action()
    }
}