package dev.gonodono.glancet.demo

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.core.widget.RemoteViewsCompat
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.fillMaxSize
import dev.gonodono.glancet.demo.internal.ActionItemClick
import dev.gonodono.glancet.demo.internal.ExtraItemPosition
import dev.gonodono.glancet.demo.internal.ItemCount
import dev.gonodono.glancet.demo.internal.SimpleNavigationFrame
import dev.gonodono.glancet.demo.internal.appWidgetIdExtra
import dev.gonodono.glancet.demo.internal.debugLog
import dev.gonodono.glancet.demo.internal.displayName
import dev.gonodono.glancet.demo.internal.setDataToSelfUri
import dev.gonodono.glancet.remoteadapter.AbsListViewState
import dev.gonodono.glancet.remoteadapter.AdapterViewAnimatorState
import dev.gonodono.glancet.remoteadapter.AdapterViewType
import dev.gonodono.glancet.remoteadapter.rememberAdapterViewFlipperState
import dev.gonodono.glancet.remoteadapter.rememberGridViewState
import dev.gonodono.glancet.remoteadapter.rememberListViewState
import dev.gonodono.glancet.remoteadapter.rememberStackViewState
import dev.gonodono.glancet.remoteadapter.remoteAdapter

class ListViewWidgetReceiver :
    RemoteAdapterWidgetReceiver(
        adapterViewType = AdapterViewType.ListView,
        layoutId = R.layout.list_view,
        adapterViewId = R.id.list_view,
        itemLayoutId = R.layout.list_view_item
    )

class GridViewWidgetReceiver :
    RemoteAdapterWidgetReceiver(
        adapterViewType = AdapterViewType.GridView,
        layoutId = R.layout.grid_view,
        adapterViewId = R.id.grid_view,
        itemLayoutId = R.layout.grid_view_item
    )

class StackViewWidgetReceiver :
    RemoteAdapterWidgetReceiver(
        adapterViewType = AdapterViewType.StackView,
        layoutId = R.layout.stack_view,
        adapterViewId = R.id.stack_view,
        itemLayoutId = R.layout.stack_view_item
    )

class AdapterViewFlipperWidgetReceiver :
    RemoteAdapterWidgetReceiver(
        adapterViewType = AdapterViewType.AdapterViewFlipper,
        layoutId = R.layout.adapter_view_flipper,
        adapterViewId = R.id.adapter_view_flipper,
        itemLayoutId = R.layout.adapter_view_flipper_item
    )

abstract class RemoteAdapterWidgetReceiver(
    internal val adapterViewType: AdapterViewType,
    private val layoutId: Int,
    private val adapterViewId: Int,
    private val itemLayoutId: Int
) : GlanceAppWidgetReceiver() {

    final override val glanceAppWidget: GlanceAppWidget
        get() = AdapterViewWidget(
            receiver = this,
            layoutId = layoutId,
            adapterViewId = adapterViewId,
            itemLayoutId = itemLayoutId
        )

    final override fun onReceive(context: Context, intent: Intent) =
        if (intent.action == ActionItemClick) {
            val name = javaClass.displayName()
            val appWidgetId = intent.appWidgetIdExtra
            val position = intent.getIntExtra(ExtraItemPosition, -1)
            debugLog("$name: appWidgetID=$appWidgetId, position=$position")
        } else {
            super.onReceive(context, intent)
        }
}

private class AdapterViewWidget(
    val receiver: RemoteAdapterWidgetReceiver,
    val layoutId: Int,
    val adapterViewId: Int,
    val itemLayoutId: Int
) : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = manager.getAppWidgetId(id)

        val clickIntent =
            Intent(ActionItemClick, null, context, receiver.javaClass)
                .apply { appWidgetIdExtra = appWidgetId }
                .setDataToSelfUri()
        val clickPending =
            PendingIntent.getBroadcast(
                /* context = */ context,
                /* requestCode = */ appWidgetId,
                /* intent = */ clickIntent,
                /* flags = */ FLAG_MUTABLE or FLAG_UPDATE_CURRENT
            )
        val remoteViews =
            RemoteViews(context.packageName, layoutId)
                .apply { setPendingIntentTemplate(adapterViewId, clickPending) }

        // These items could easily be switched out with an Intent for
        // RemoteAdapterWidgetService (located in the `internal` subpackage), or
        // with the platform version of RemoteCollectionItems on API levels 31+.
        val adapterItems =
            RemoteViewsCompat.RemoteCollectionItems.Builder().run {
                repeat(ItemCount) { position ->
                    val views = RemoteViews(context.packageName, itemLayoutId)
                    views.setTextViewText(R.id.text, "#$position")

                    val click = Intent()
                        .apply { appWidgetIdExtra = appWidgetId }
                        .putExtra(ExtraItemPosition, position)
                    views.setOnClickFillInIntent(R.id.text, click)

                    addItem(position.toLong(), views)
                }
                build()
            }

        provideContent {
            RemoteAdapterWidgetContent(
                remoteViews = remoteViews,
                adapterViewId = adapterViewId,
                adapterViewType = receiver.adapterViewType,
                adapterItems = adapterItems
            )
        }
    }
}

@Composable
private fun RemoteAdapterWidgetContent(
    remoteViews: RemoteViews,
    adapterViewId: Int,
    adapterViewType: AdapterViewType,
    adapterItems: RemoteViewsCompat.RemoteCollectionItems
) {
    val adapterState =
        when (adapterViewType) {
            AdapterViewType.ListView -> {
                rememberListViewState(adapterViewId, adapterItems)
            }
            AdapterViewType.GridView -> {
                rememberGridViewState(adapterViewId, adapterItems)
            }
            AdapterViewType.StackView -> {
                rememberStackViewState(adapterViewId, adapterItems)
            }
            AdapterViewType.AdapterViewFlipper -> {
                rememberAdapterViewFlipperState(adapterViewId, adapterItems)
            }
        }

    SimpleNavigationFrame(
        onNext = {
            (adapterState as? AbsListViewState)?.smoothScrollByOffset(2)
            (adapterState as? AdapterViewAnimatorState)?.showNext()
        },
        onPrevious = {
            (adapterState as? AbsListViewState)?.smoothScrollByOffset(-2)
            (adapterState as? AdapterViewAnimatorState)?.showPrevious()
        }
    ) {
        AndroidRemoteViews(
            remoteViews = remoteViews,
            modifier = GlanceModifier
                .remoteAdapter(adapterState)
                .fillMaxSize()
        )
    }
}