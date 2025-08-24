package dev.gonodono.glancet.remoteadapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.core.widget.RemoteViewsCompat
import androidx.glance.Emittable
import androidx.glance.GlanceModifier
import androidx.glance.findModifier

/**
 * Attaches a [RemoteAdapterState] to the Composable's [GlanceModifier] chain in
 * order to allow for its later application by code injected into Glance.
 */
public fun GlanceModifier.remoteAdapter(state: RemoteAdapterState): GlanceModifier =
    this then RemoteAdapterModifier(state as RemoteAdapterStateImpl)

private class RemoteAdapterModifier(val state: RemoteAdapterStateImpl) :
    GlanceModifier.Element {

    init {
        // The actual read for this occurs during RemoteViews construction,
        // which apparently happens outside of measure/layout/draw, so we
        // need to read it ourselves in order to trigger recomposition.
        state.action
    }
}

internal sealed interface RemoteAdapter {

    val adapterViewId: Int

    sealed interface Items : RemoteAdapter {

        data class Compat(
            override val adapterViewId: Int,
            val context: Context,
            val appWidgetId: Int,
            val items: RemoteViewsCompat.RemoteCollectionItems
        ) : Items

        data class Platform(
            override val adapterViewId: Int,
            val items: RemoteViews.RemoteCollectionItems
        ) : Items
    }

    data class Intents(
        override val adapterViewId: Int,
        val intent: Intent
    ) : RemoteAdapter
}

// NB: This must remain in the same file as the corresponding GlanceModifier
// extension function in order to ensure that Proguard/R8 handles it correctly.
@SuppressLint("RestrictedApi")
@JvmName("setRemoteAdapterIfPresent")
internal fun RemoteViews.setRemoteAdapterIfPresent(element: Emittable) {
    val modifier =
        element.modifier.findModifier<RemoteAdapterModifier>() ?: return

    when (val adapter = modifier.state.adapter) {
        is RemoteAdapter.Items.Compat -> {
            RemoteViewsCompat.setRemoteAdapter(
                context = adapter.context,
                remoteViews = this,
                appWidgetId = adapter.appWidgetId,
                viewId = adapter.adapterViewId,
                items = adapter.items
            )
        }
        is RemoteAdapter.Items.Platform -> {
            if (Build.VERSION.SDK_INT < 31) return
            RemoteAdapterHelper.setRemoteAdapter(
                remoteViews = this,
                adapterViewId = adapter.adapterViewId,
                items = adapter.items
            )
        }
        is RemoteAdapter.Intents -> {
            @Suppress("DEPRECATION")
            setRemoteAdapter(adapter.adapterViewId, adapter.intent)
        }
    }

    modifier.state.action?.invoke(this)
}

@RequiresApi(31)
private object RemoteAdapterHelper {

    @DoNotInline
    fun setRemoteAdapter(
        remoteViews: RemoteViews,
        adapterViewId: Int,
        items: RemoteViews.RemoteCollectionItems
    ) =
        remoteViews.setRemoteAdapter(adapterViewId, items)
}