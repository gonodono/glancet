package dev.gonodono.glimpse.scrollablelazy

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glimpse.TestRemoteViews
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class ScrollableLazyStateTest {

    @Config(sdk = [31])
    @Test
    fun scrollableLazyStateSetActionApi31() {
        val state = ScrollableLazyState() as ScrollableLazyStateImpl

        val remoteViews = TestRemoteViews()
        remoteViews.assertInvokedCountEquals(0)

        state.smoothScrollToPosition(0)
        remoteViews.assertInvokedCountEquals(0)

        state.requireAction().invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(1)

        state.requireAction().invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(1)

        state.smoothScrollToPosition(0)
        remoteViews.assertInvokedCountEquals(1)

        state.requireAction().invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(2)

        state.requireAction().invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(2)
    }

    @Test
    fun scrollableLazyStateSetActionApiMin() {
        val state = ScrollableLazyState() as ScrollableLazyStateImpl

        val remoteViews = TestRemoteViews()
        remoteViews.assertInvokedCountEquals(0)

        state.smoothScrollToPosition(0)
        assertNull("action is not null", state.action)
        remoteViews.assertInvokedCountEquals(0)

        state.smoothScrollByOffset(0)
        assertNull("action is not null", state.action)
        remoteViews.assertInvokedCountEquals(0)
    }
}

private fun ScrollableLazyStateImpl.requireAction(): ScrollableLazyAction {
    assertNotNull("action is null", action)
    return action!!
}