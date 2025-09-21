package dev.gonodono.glimpse.demo

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import dev.gonodono.glimpse.demo.internal.ItemCount
import dev.gonodono.glimpse.demo.internal.SimpleNavigationFrame
import dev.gonodono.glimpse.demo.internal.debugLog
import dev.gonodono.glimpse.demo.internal.isEven
import dev.gonodono.glimpse.scrollablelazy.ScrollableLazyColumn
import dev.gonodono.glimpse.scrollablelazy.ScrollableLazyVerticalGrid
import dev.gonodono.glimpse.scrollablelazy.rememberScrollableLazyState

class ScrollableLazyColumnWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScrollableLazyColumnWidget()
}

private class ScrollableLazyColumnWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        provideContent { ScrollableLazyColumnContent(appWidgetId) }
    }
}

@Composable
private fun ScrollableLazyColumnContent(appWidgetId: Int) {

    val state = rememberScrollableLazyState()

    SimpleNavigationFrame(
        onNext = { state.smoothScrollByOffset(2) },
        onPrevious = { state.smoothScrollByOffset(-2) }
    ) {
        ScrollableLazyColumn(
            state = state,
            modifier = GlanceModifier.fillMaxSize()
        ) {
            items(count = ItemCount, itemId = Int::toLong) { index ->
                val bottom = if (index < ItemCount - 1) 10.dp else 0.dp
                Box(modifier = GlanceModifier.padding(bottom = bottom)) {
                    ItemText(
                        name = "ScrollableLazyColumn",
                        appWidgetId = appWidgetId,
                        index = index,
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
            }
        }
    }
}

class ScrollableLazyVerticalGridWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget =
        ScrollableLazyVerticalGridWidget()
}

private class ScrollableLazyVerticalGridWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        provideContent { ScrollableLazyVerticalGridContent(appWidgetId) }
    }
}

@Composable
private fun ScrollableLazyVerticalGridContent(appWidgetId: Int) {

    val state = rememberScrollableLazyState()

    SimpleNavigationFrame(
        onNext = { state.smoothScrollByOffset(2) },
        onPrevious = { state.smoothScrollByOffset(-2) }
    ) {
        ScrollableLazyVerticalGrid(
            state = state,
            gridCells = GridCells.Fixed(2),
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = ItemCount, itemId = Int::toLong) { index ->
                val modifier =
                    GlanceModifier.padding(
                        start = if (index.isEven) 0.dp else 5.dp,
                        end = if (index.isEven) 5.dp else 0.dp,
                        bottom = if (index < ItemCount - 1) 10.dp else 0.dp
                    )
                Box(modifier = modifier) {
                    ItemText(
                        name = "ScrollableLazyVerticalGrid",
                        appWidgetId = appWidgetId,
                        index = index,
                        modifier = GlanceModifier.size(60.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemText(
    name: String,
    appWidgetId: Int,
    index: Int,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(ImageProvider(R.drawable.item_background))
            .clickable { debugLog(name, appWidgetId, index) }
    ) {
        Text(text = "#$index")
    }
}