package dev.gonodono.glancet.lazycompat

import android.os.Bundle
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.lazy.GridCells
import dev.gonodono.glancet.assertModifierPresent
import org.junit.Test

class LazyVerticalGridCompatTest {

    @Test
    fun baseHasLazyCompatModifier() =
        assertModifierPresent(LazyCompatModifier::class.java) { taggedModifier ->
            LazyVerticalGridCompat(
                state = rememberLazyCompatState(),
                gridCells = GridCells.Fixed(1),
                modifier = taggedModifier
            ) {}
        }

    @OptIn(ExperimentalGlanceApi::class)
    @Test
    fun overloadHasLazyCompatModifier() =
        assertModifierPresent(LazyCompatModifier::class.java) { taggedModifier ->
            LazyVerticalGridCompat(
                state = rememberLazyCompatState(),
                gridCells = GridCells.Fixed(1),
                modifier = taggedModifier,
                activityOptions = Bundle()
            ) {}
        }
}