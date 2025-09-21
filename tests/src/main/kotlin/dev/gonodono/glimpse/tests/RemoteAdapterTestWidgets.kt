package dev.gonodono.glimpse.tests

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.core.widget.RemoteViewsCompat
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import dev.gonodono.glimpse.remoteadapter.AbsListViewState
import dev.gonodono.glimpse.remoteadapter.AdapterViewAnimatorState
import dev.gonodono.glimpse.remoteadapter.RemoteAdapterState
import dev.gonodono.glimpse.remoteadapter.rememberListViewState
import dev.gonodono.glimpse.remoteadapter.rememberStackViewState
import dev.gonodono.glimpse.remoteadapter.remoteAdapter

class ListViewWidget : RemoteAdapterWidget<AbsListViewState>() {

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget = ListViewWidget()
    }

    override val layoutId: Int = R.layout.list_view

    @Composable
    override fun rememberState(items: RemoteViewsCompat.RemoteCollectionItems) =
        rememberListViewState(R.id.list_view, items)

    @Composable
    override fun ScrollButtons(state: AbsListViewState) {
        // Position must be for an off-screen item in order to ensure scroll.
        Button(ToPosition, { state.smoothScrollToPosition(ItemCount - 1) })
        Button(ByOffset, { state.smoothScrollByOffset(2) })
    }
}

class StackViewWidget : RemoteAdapterWidget<AdapterViewAnimatorState>() {

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget = StackViewWidget()
    }

    override val layoutId: Int = R.layout.stack_view

    @Composable
    override fun rememberState(items: RemoteViewsCompat.RemoteCollectionItems) =
        rememberStackViewState(R.id.stack_view, items)

    @Composable
    override fun ScrollButtons(state: AdapterViewAnimatorState) {
        Button(DisplayedChild, { state.setDisplayedChild(1) })
        Button(ShowNext, { state.showNext() })
    }
}

abstract class RemoteAdapterWidget<S : RemoteAdapterState> : GlanceAppWidget() {

    abstract val layoutId: Int

    @Composable
    abstract fun rememberState(items: RemoteViewsCompat.RemoteCollectionItems): S

    @Composable
    abstract fun ScrollButtons(state: S)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val packageName = context.packageName

        val items =
            RemoteViewsCompat.RemoteCollectionItems.Builder().run {
                repeat(ItemCount) { position ->
                    val item = RemoteViews(packageName, R.layout.item)
                    item.setTextViewText(R.id.text, "#$position")
                    addItem(position.toLong(), item)
                }
                build()
            }

        val remoteViews = RemoteViews(packageName, layoutId)

        provideContent {
            val state = rememberState(items)

            Column {
                AndroidRemoteViews(
                    remoteViews = remoteViews,
                    modifier = GlanceModifier
                        .remoteAdapter(state)
                        .fillMaxWidth()
                        .defaultWeight()
                        .background(ImageProvider(R.drawable.outline_background))
                )

                ScrollButtons(state)
            }
        }
    }
}