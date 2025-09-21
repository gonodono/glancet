package dev.gonodono.glimpse.scrollablelazy

import android.os.Bundle
import androidx.glance.ExperimentalGlanceApi
import dev.gonodono.glimpse.assertModifierPresent
import org.junit.Test

class ScrollableLazyGridTest {

    @Test
    fun baseHasScrollableLazyModifier() =
        assertModifierPresent(ScrollableLazyModifier::class.java) { taggedModifier ->
            ScrollableLazyColumn(
                state = rememberScrollableLazyState(),
                modifier = taggedModifier
            ) {}
        }

    @OptIn(ExperimentalGlanceApi::class)
    @Test
    fun overloadHasScrollableLazyModifier() =
        assertModifierPresent(ScrollableLazyModifier::class.java) { taggedModifier ->
            ScrollableLazyColumn(
                state = rememberScrollableLazyState(),
                activityOptions = Bundle(),
                modifier = taggedModifier
            ) {}
        }
}