package dev.gonodono.glancet.remoteadapter

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glancet.TestRemoteViews
import dev.gonodono.glancet.TestRemoteViewsAction
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteAdapterActionTest {

    @Test
    fun invokesOnlyOnce() {
        val action = RemoteAdapterAction(0, TestRemoteViewsAction)

        val remoteViews = TestRemoteViews()
        assertEquals("Invoked count != 0", remoteViews.invokedCount, 0)

        action.invoke(remoteViews)
        assertEquals("Invoked count != 1", remoteViews.invokedCount, 1)

        action.invoke(remoteViews)
        assertEquals("Invoked count != 1", remoteViews.invokedCount, 1)

        action.invoke(remoteViews)
        assertEquals("Invoked count != 1", remoteViews.invokedCount, 1)

        action.invoke(remoteViews)
        assertEquals("Invoked count != 1", remoteViews.invokedCount, 1)
    }
}