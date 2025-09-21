package dev.gonodono.glimpse.scrollablelazy

import android.os.Bundle
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.lazy.GridCells
import dev.gonodono.glimpse.assertModifierPresent
import org.junit.Test

class ScrollableLazyVerticalGridTest {

    @Test
    fun baseHasScrollableLazyModifier() =
        assertModifierPresent(ScrollableLazyModifier::class.java) { taggedModifier ->
            ScrollableLazyVerticalGrid(
                state = rememberScrollableLazyState(),
                gridCells = GridCells.Fixed(1),
                modifier = taggedModifier
            ) {}
        }

    @OptIn(ExperimentalGlanceApi::class)
    @Test
    fun overloadHasScrollableLazyModifier() =
        assertModifierPresent(ScrollableLazyModifier::class.java) { taggedModifier ->
            ScrollableLazyVerticalGrid(
                state = rememberScrollableLazyState(),
                gridCells = GridCells.Fixed(1),
                modifier = taggedModifier,
                activityOptions = Bundle()
            ) {}
        }
}