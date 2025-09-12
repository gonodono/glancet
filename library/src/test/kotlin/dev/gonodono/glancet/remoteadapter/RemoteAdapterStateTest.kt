package dev.gonodono.glancet.remoteadapter

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class RemoteAdapterStateTest {

    @Config(sdk = [31])
    @Test
    fun remoteAdapterStateApi31SetAction() {
        val state = AbsListViewState(RemoteAdapter.Intents(0, Intent()))
        state.smoothScrollToPosition(0)

        state as RemoteAdapterStateImpl
        assertNotNull("action is null", state.action)
    }

    @Test
    fun remoteAdapterStateApi31SetActionApiMin() {
        val state = AbsListViewState(RemoteAdapter.Intents(0, Intent()))
        state.smoothScrollToPosition(0)

        state as RemoteAdapterStateImpl
        assertNull("action is not null", state.action)
    }
}