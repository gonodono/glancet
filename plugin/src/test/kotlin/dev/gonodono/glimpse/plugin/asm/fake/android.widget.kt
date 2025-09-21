@file:Suppress("PackageDirectoryMismatch")

package android.widget

import org.junit.Assert.assertFalse

internal class RemoteViews {

    var isUsed: Boolean = false
        private set(value) {
            assertFalse("RemoteViews are single use only", isUsed)
            field = value
        }

    fun setUsed() {
        isUsed = true
    }
}