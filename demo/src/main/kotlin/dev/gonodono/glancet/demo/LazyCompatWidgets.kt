package dev.gonodono.glancet.demo

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
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
import dev.gonodono.glancet.demo.internal.ItemCount
import dev.gonodono.glancet.demo.internal.SimpleNavigationFrame
import dev.gonodono.glancet.demo.internal.isEven
import dev.gonodono.glancet.lazycompat.LazyColumnCompat
import dev.gonodono.glancet.lazycompat.LazyVerticalGridCompat
import dev.gonodono.glancet.lazycompat.rememberLazyCompatState

class LazyColumnCompatWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LazyColumnCompatWidget()
}

private class LazyColumnCompatWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId): Unit =
        provideContent { LazyColumnCompatContent() }
}

@Composable
private fun LazyColumnCompatContent() {

    val state = rememberLazyCompatState()

    SimpleNavigationFrame(
        onNext = { state.smoothScrollByOffset(2) },
        onPrevious = { state.smoothScrollByOffset(-2) }
    ) {
        LazyColumnCompat(
            state = state,
            modifier = GlanceModifier.fillMaxSize()
        ) {
            items(count = ItemCount, itemId = Int::toLong) { index ->
                val bottom = if (index < ItemCount - 1) 10.dp else 0.dp
                Box(modifier = GlanceModifier.padding(bottom = bottom)) {
                    ItemText(
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

class LazyVerticalGridCompatWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget =
        LazyVerticalGridCompatWidget()
}

private class LazyVerticalGridCompatWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId): Unit =
        provideContent { LazyVerticalGridCompatContent() }
}

@Composable
private fun LazyVerticalGridCompatContent() {

    val state = rememberLazyCompatState()

    SimpleNavigationFrame(
        onNext = { state.smoothScrollByOffset(2) },
        onPrevious = { state.smoothScrollByOffset(-2) }
    ) {
        LazyVerticalGridCompat(
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
    index: Int,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(ImageProvider(R.drawable.item_background))
    ) {
        Text(text = "#$index")
    }
}