package dev.gonodono.glancet.lazycompat

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glancet.TestRemoteViews
import dev.gonodono.glancet.TestRemoteViewsAction
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LazyCompatActionTest {

    @Test
    fun invokesOnlyOnce() {
        val action = LazyCompatAction(TestRemoteViewsAction)

        val remoteViews = TestRemoteViews()
        remoteViews.assertInvokedCountEquals(0)

        action.invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(1)

        action.invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(1)

        action.invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(1)

        action.invoke(remoteViews, 0)
        remoteViews.assertInvokedCountEquals(1)
    }
}