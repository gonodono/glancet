package dev.gonodono.glimpse.demo.internal

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import dev.gonodono.glimpse.demo.R

// Quick and hacky one-off. Do not use in a real app!
@Composable
internal fun SimpleNavigationFrame(
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: GlanceModifier = GlanceModifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .appWidgetBackground()
            .fillMaxSize()
            .background(R.color.widget_background)
            .padding(10.dp)
    ) {
        if (Build.VERSION.SDK_INT < 31) run { content(); return@Column }

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight(),
            content = content
        )

        Spacer(modifier = GlanceModifier.size(10.dp))

        Row(modifier = GlanceModifier.fillMaxWidth()) {
            // False positive on K2.
            @SuppressLint("RestrictedApi")
            val backgroundColor = ColorProvider(Color(0xFFD6D7D7))
            val buttonColors = ButtonDefaults.buttonColors(backgroundColor)

            Button(
                text = "<-",
                colors = buttonColors,
                onClick = onPrevious,
                modifier = GlanceModifier.defaultWeight()
            )

            Spacer(modifier = GlanceModifier.size(10.dp))

            Button(
                text = "->",
                colors = buttonColors,
                onClick = onNext,
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}