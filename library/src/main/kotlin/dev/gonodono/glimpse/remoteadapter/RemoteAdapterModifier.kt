package dev.gonodono.glimpse.remoteadapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.core.widget.RemoteViewsCompat
import androidx.glance.GlanceModifier
import dev.gonodono.glimpse.find

/**
 * Attaches a [RemoteAdapterState] to the Composable's [GlanceModifier] chain in
 * order to allow for its later application by code injected into Glance.
 */
public fun GlanceModifier.remoteAdapter(state: RemoteAdapterState): GlanceModifier =
    this then RemoteAdapterModifier(state as RemoteAdapterStateImpl)

internal class RemoteAdapterModifier(val state: RemoteAdapterStateImpl) :
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

    fun applyTo(remoteViews: RemoteViews)

    sealed interface Items : RemoteAdapter {

        data class Compat(
            override val adapterViewId: Int,
            val context: Context,
            val appWidgetId: Int,
            val items: RemoteViewsCompat.RemoteCollectionItems
        ) : Items {
            override fun applyTo(remoteViews: RemoteViews) {
                RemoteViewsCompat.setRemoteAdapter(
                    context = context,
                    remoteViews = remoteViews,
                    appWidgetId = appWidgetId,
                    viewId = adapterViewId,
                    items = items
                )
            }
        }

        data class Platform(
            override val adapterViewId: Int,
            val items: RemoteViews.RemoteCollectionItems
        ) : Items {
            override fun applyTo(remoteViews: RemoteViews) {
                if (Build.VERSION.SDK_INT < 31) return
                RemoteAdapterHelper.setRemoteAdapter(
                    remoteViews = remoteViews,
                    adapterViewId = adapterViewId,
                    items = items
                )
            }
        }
    }

    data class Intents(
        override val adapterViewId: Int,
        val intent: Intent
    ) : RemoteAdapter {
        override fun applyTo(remoteViews: RemoteViews) {
            @Suppress("DEPRECATION")
            remoteViews.setRemoteAdapter(adapterViewId, intent)
        }
    }
}

// NB: This must remain in the same file as the corresponding GlanceModifier
// extension function in order to ensure that Proguard/R8 handles it correctly.
@JvmName("setRemoteAdapterIfPresent")
internal fun RemoteViews.setRemoteAdapterIfPresent(modifier: GlanceModifier) {
    val remoteAdapter = modifier.find<RemoteAdapterModifier>() ?: return

    val state = remoteAdapter.state
    state.adapter.applyTo(this)
    state.action?.invoke(this)
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