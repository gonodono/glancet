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
 * The state interface for [StackView][android.widget.StackView] and
 * [AdapterViewFlipper][android.widget.AdapterViewFlipper].
 */
public interface AdapterViewAnimatorState : RemoteAdapterState {

    /**
     * Translates to [RemoteViews.setDisplayedChild].
     *
     * **NB:** This function has no effect on API levels 30 and below. Glance
     * performs only full updates, and the platform didn't start preserving the
     * relevant state across such updates until API level 31.
     */
    public fun setDisplayedChild(whichChild: Int)

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
}


/**
 * Creates and remembers the [remoteAdapter] state for a
 * [StackView][android.widget.StackView] populated with
 * [androidx.core.widget.RemoteViewsCompat.RemoteCollectionItems].
 */
@Composable
public fun rememberStackViewState(
    @IdRes stackViewId: Int,
    items: RemoteViewsCompat.RemoteCollectionItems
): AdapterViewAnimatorState {
    val context = LocalContext.current
    val glanceId = LocalGlanceId.current

    return remember(stackViewId, items, context, glanceId) {
        val appWidgetManager = GlanceAppWidgetManager(context)
        val appWidgetId = appWidgetManager.getAppWidgetId(glanceId)
        val adapter =
            RemoteAdapter.Items.Compat(
                adapterViewId = stackViewId,
                context = context,
                appWidgetId = appWidgetId,
                items = items
            )
        AdapterViewAnimatorState(adapter)
    }
}

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [StackView][android.widget.StackView] populated with
 * [android.widget.RemoteViews.RemoteCollectionItems].
 */
@RequiresApi(31)
@Composable
public fun rememberStackViewState(
    @IdRes stackViewId: Int,
    items: RemoteViews.RemoteCollectionItems
): AdapterViewAnimatorState =
    remember(stackViewId, items) {
        val adapter = RemoteAdapter.Items.Platform(stackViewId, items)
        AdapterViewAnimatorState(adapter)
    }

/**
 * Creates and remembers the [remoteAdapter] state for a
 * [StackView][android.widget.StackView] populated with a [android.widget.RemoteViewsService].
 */
@Composable
public fun rememberStackViewState(
    @IdRes stackViewId: Int,
    intent: Intent
): AdapterViewAnimatorState =
    remember(stackViewId, intent) {
        val adapter = RemoteAdapter.Intents(stackViewId, intent)
        AdapterViewAnimatorState(adapter)
    }


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
 * [android.widget.RemoteViewsService].
 */
@Composable
public fun rememberAdapterViewFlipperState(
    @IdRes adapterViewFlipperId: Int,
    intent: Intent
): AdapterViewAnimatorState =
    rememberStackViewState(adapterViewFlipperId, intent)


internal fun AdapterViewAnimatorState(adapter: RemoteAdapter): AdapterViewAnimatorState =
    if (Build.VERSION.SDK_INT >= 31) {
        AdapterViewAnimatorStateApi31(adapter)
    } else {
        AdapterViewAnimatorStateApiMin(adapter)
    }

@RequiresApi(31)
private class AdapterViewAnimatorStateApi31(adapter: RemoteAdapter) :
    RemoteAdapterStateApi31(adapter),
    AdapterViewAnimatorState {

    override fun setDisplayedChild(whichChild: Int) =
        setAction { setDisplayedChild(it, whichChild) }

    override fun showNext() =
        setAction { @Suppress("DEPRECATION") showNext(it) }

    override fun showPrevious() =
        setAction { @Suppress("DEPRECATION") showPrevious(it) }
}

private class AdapterViewAnimatorStateApiMin(adapter: RemoteAdapter) :
    RemoteAdapterStateApiMin(adapter),
    AdapterViewAnimatorState {

    override fun setDisplayedChild(whichChild: Int) {}
    override fun showNext() {}
    override fun showPrevious() {}
}