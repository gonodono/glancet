package dev.gonodono.glancet.tests

import android.content.Context
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.wrapContentSize
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import dev.gonodono.glancet.lazycompat.LazyColumnCompat
import dev.gonodono.glancet.lazycompat.rememberLazyCompatState

class LazyCompatWidget : GlanceAppWidget() {

    class Receiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget = LazyCompatWidget()
    }

    override suspend fun provideGlance(context: Context, id: GlanceId): Unit =
        provideContent {

            val state = rememberLazyCompatState()
            val background = ImageProvider(R.drawable.outline_background)

            Column {
                LazyColumnCompat(
                    state = state,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                        .background(background)
                ) {
                    val style =
                        TextDefaults.defaultTextStyle.copy(fontSize = 22.sp)

                    items(count = ItemCount, itemId = Int::toLong) {
                        Text(
                            text = "#$it",
                            style = style,
                            modifier = GlanceModifier
                                .wrapContentSize()
                                .background(background)
                        )
                    }
                }
                Button(
                    text = ToPosition,
                    onClick = { state.smoothScrollToPosition(ItemCount - 1) }
                )
                Button(
                    text = ByOffset,
                    onClick = { state.smoothScrollByOffset(2) }
                )
            }
        }
}