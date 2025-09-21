package dev.gonodono.glimpse.scrollablelazy

import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glimpse.TestRemoteViews
import dev.gonodono.glimpse.assertModifierPresent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class ScrollableLazyModifierTest {

    @Test
    fun scrollableLazyExtensionCreatesModifier() =
        assertModifierPresent(ScrollableLazyModifier::class.java) { taggedModifier ->
            LazyColumn(taggedModifier.scrollableLazy(rememberScrollableLazyState())) {}
        }

    @Config(sdk = [31])
    @Test
    fun applyScrollableLazyIfPresentApi31() {
        val state = ScrollableLazyState()
        state.smoothScrollToPosition(0)

        val remoteViews = TestRemoteViews()
        val modifier = GlanceModifier.scrollableLazy(state)
        remoteViews.applyScrollableLazyIfPresent(modifier, 0)
        remoteViews.assertInvokedCountEquals(1)
    }

    @Test
    fun applyScrollableLazyIfPresentApiMin() {
        val state = ScrollableLazyState()
        state.smoothScrollToPosition(0)

        val remoteViews = TestRemoteViews()
        val modifier = GlanceModifier.scrollableLazy(state)
        remoteViews.applyScrollableLazyIfPresent(modifier, 0)
        remoteViews.assertInvokedCountEquals(0)
    }
}