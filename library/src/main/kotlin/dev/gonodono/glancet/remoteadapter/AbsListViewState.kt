package dev.gonodono.glancet.remoteadapter

import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.widget.RemoteViewsCompat
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager

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
 * Creates and remembers the [remoteAdapter] state for a
 * [ListView][android.widget.ListView] populated with
 * [androidx.core.widget.RemoteViewsCompat.RemoteCollectionItems].
 */
@Composable
public fun rememberListViewState(
    @IdRes listViewId: Int,
    items: RemoteViewsCompat.RemoteCollectionItems
): AbsListViewState {
    val context = LocalContext.current
    val glanceId = LocalGlanceId.current

    return remember(listViewId, items, context, glanceId) {
        val appWidgetManager = GlanceAppWidgetManager(context)
        val appWidgetId = appWidgetManager.getAppWidgetId(glanceId)
        val adapter =
            RemoteAdapter.Items.Compat(
                adapterViewId = listViewId,
                context = context,
                appWidgetId = appWidgetId,
                items = items
            )
        AbsListViewState(adapter)
    }
}

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [ListView][android.widget.ListView] populated with
 * [android.widget.RemoteViews.RemoteCollectionItems].
 */
@RequiresApi(31)
@Composable
public fun rememberListViewState(
    @IdRes listViewId: Int,
    items: RemoteViews.RemoteCollectionItems
): AbsListViewState =
    remember(listViewId, items) {
        val adapter = RemoteAdapter.Items.Platform(listViewId, items)
        AbsListViewState(adapter)
    }

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [ListView][android.widget.ListView] populated with a [android.widget.RemoteViewsService].
 */
@Composable
public fun rememberListViewState(
    @IdRes listViewId: Int,
    intent: Intent
): AbsListViewState =
    remember(listViewId, intent) {
        val adapter = RemoteAdapter.Intents(listViewId, intent)
        AbsListViewState(adapter)
    }


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
 * [GridView][android.widget.GridView] populated with a [android.widget.RemoteViewsService].
 */
@Composable
public fun rememberGridViewState(
    @IdRes gridViewId: Int,
    intent: Intent
): AbsListViewState =
    rememberListViewState(gridViewId, intent)


internal fun AbsListViewState(adapter: RemoteAdapter): AbsListViewState =
    if (Build.VERSION.SDK_INT >= 31) {
        AbsListViewStateApi31(adapter)
    } else {
        AbsListViewStateApiMin(adapter)
    }

@RequiresApi(31)
private class AbsListViewStateApi31(adapter: RemoteAdapter) :
    RemoteAdapterStateApi31(adapter),
    AbsListViewState {

    override fun smoothScrollToPosition(position: Int) =
        setAction { setInt(it, "smoothScrollToPosition", position) }

    override fun smoothScrollByOffset(offset: Int) =
        setAction { setInt(it, "smoothScrollByOffset", offset) }
}

private class AbsListViewStateApiMin(adapter: RemoteAdapter) :
    RemoteAdapterStateApiMin(adapter),
    AbsListViewState {

    override fun smoothScrollToPosition(position: Int) {}
    override fun smoothScrollByOffset(offset: Int) {}
}