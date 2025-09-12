package dev.gonodono.glancet.remoteadapter

import android.content.Intent
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.gonodono.glancet.TestRemoteViews
import dev.gonodono.glancet.assertModifierPresent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class RemoteAdapterModifierTest {

    @Test
    fun remoteAdapterExtensionCreatesModifier() =
        assertModifierPresent(RemoteAdapterModifier::class.java) { taggedModifier ->
            val state = rememberListViewState(0, Intent())
            LazyColumn(taggedModifier.remoteAdapter(state)) {}
        }

    @Config(sdk = [31])
    @Test
    fun setRemoteAdapterIfPresentApi31() {
        val state = AbsListViewState(RemoteAdapter.Intents(0, Intent()))
        state.smoothScrollToPosition(0)

        val remoteViews = TestRemoteViews()
        val modifier = GlanceModifier.remoteAdapter(state)
        remoteViews.setRemoteAdapterIfPresent(modifier)
        remoteViews.assertInvokedCountEquals(1)
    }

    @Test
    fun setRemoteAdapterIfPresentApiMin() {
        val state = AbsListViewState(RemoteAdapter.Intents(0, Intent()))
        state.smoothScrollToPosition(0)

        val remoteViews = TestRemoteViews()
        val modifier = GlanceModifier.remoteAdapter(state)
        remoteViews.setRemoteAdapterIfPresent(modifier)
        remoteViews.assertInvokedCountEquals(0)
    }
}