package dev.gonodono.glancet

import android.widget.RemoteViews
import org.junit.Assert.assertEquals

internal val TestRemoteViewsAction: RemoteViews.(Int) -> Unit =
    { setInt(it, "", 0) }

internal class TestRemoteViews :
    RemoteViews(ApplicationContext.packageName, 0) {

    var invokedCount = 0
        private set

    override fun setInt(viewId: Int, methodName: String, value: Int) {
        invokedCount++
    }

    fun assertInvokedCountEquals(count: Int) {
        assertEquals("Invoked count != $count", invokedCount, count)
    }
}