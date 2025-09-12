package dev.gonodono.glancet.lazycompat

import android.os.Bundle
import androidx.glance.ExperimentalGlanceApi
import dev.gonodono.glancet.assertModifierPresent
import org.junit.Test

class LazyColumnCompatTest {

    @Test
    fun baseHasLazyCompatModifier() =
        assertModifierPresent(LazyCompatModifier::class.java) { taggedModifier ->
            LazyColumnCompat(
                state = rememberLazyCompatState(),
                modifier = taggedModifier
            ) {}
        }

    @OptIn(ExperimentalGlanceApi::class)
    @Test
    fun overloadHasLazyCompatModifier() =
        assertModifierPresent(LazyCompatModifier::class.java) { taggedModifier ->
            LazyColumnCompat(
                state = rememberLazyCompatState(),
                activityOptions = Bundle(),
                modifier = taggedModifier
            ) {}
        }
}