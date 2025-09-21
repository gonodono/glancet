package dev.gonodono.glimpse.scrollablelazy

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glimpse.TestRemoteViews
import dev.gonodono.glimpse.TestRemoteViewsAction
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollableLazyActionTest {

    @Test
    fun invokesOnlyOnce() {
        val action = ScrollableLazyAction(TestRemoteViewsAction)

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