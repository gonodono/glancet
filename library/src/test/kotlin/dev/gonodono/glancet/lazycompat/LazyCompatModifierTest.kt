package dev.gonodono.glancet.lazycompat

import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glancet.TestRemoteViews
import dev.gonodono.glancet.assertModifierPresent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class LazyCompatModifierTest {

    @Test
    fun lazyCompatExtensionCreatesModifier() =
        assertModifierPresent(LazyCompatModifier::class.java) { taggedModifier ->
            LazyColumn(taggedModifier.lazyCompat(rememberLazyCompatState())) {}
        }

    @Config(sdk = [31])
    @Test
    fun applyLazyCompatIfPresentApi31() {
        val state = LazyCompatState()
        state.smoothScrollToPosition(0)

        val remoteViews = TestRemoteViews()
        val modifier = GlanceModifier.lazyCompat(state)
        remoteViews.applyLazyCompatIfPresent(modifier, 0)
        remoteViews.assertInvokedCountEquals(1)
    }

    @Test
    fun applyLazyCompatIfPresentApiMin() {
        val state = LazyCompatState()
        state.smoothScrollToPosition(0)

        val remoteViews = TestRemoteViews()
        val modifier = GlanceModifier.lazyCompat(state)
        remoteViews.applyLazyCompatIfPresent(modifier, 0)
        remoteViews.assertInvokedCountEquals(0)
    }
}