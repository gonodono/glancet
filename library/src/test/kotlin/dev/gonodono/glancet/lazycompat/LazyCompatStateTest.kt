package dev.gonodono.glancet.lazycompat

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glancet.TestRemoteViews
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class LazyCompatStateTest {

    @Config(sdk = [31])
    @Test
    fun lazyCompatStateSetActionApi31() {
        val state = LazyCompatState() as LazyCompatStateImpl

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
    fun lazyCompatStateSetActionApiMin() {
        val state = LazyCompatState() as LazyCompatStateImpl

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

private fun LazyCompatStateImpl.requireAction(): LazyCompatAction {
    assertNotNull("action is null", action)
    return action!!
}