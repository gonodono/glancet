package dev.gonodono.glancet.tests

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_BIND
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_PROVIDER
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.content.IntentCompat
import androidx.core.view.get
import androidx.core.view.isEmpty
import java.util.concurrent.Executors

class AppWidgetHostActivity : Activity() {

    private lateinit var host: AppWidgetHost

    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        host = AppWidgetHost(this, 137).apply { startListening() }

        container =
            FrameLayout(this).also { frame ->
                val params = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                setContentView(frame, params)
                frame.applyInsetsListener()
            }

        val id = host.allocateAppWidgetId()

        val provider =
            IntentCompat.getParcelableExtra(
                intent,
                ExtraProvider,
                ComponentName::class.java
            )
        checkNotNull(provider) { "Missing provider name on Intent" }

        if (appWidgetManager.bindAppWidgetIdIfAllowed(id, provider)) {
            setWidget(id)
        } else {
            val request =
                Intent(ACTION_APPWIDGET_BIND)
                    .putExtra(EXTRA_APPWIDGET_ID, id)
                    .putExtra(EXTRA_APPWIDGET_PROVIDER, provider)
            startActivityForResult(request, 137)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode != 137 || resultCode != RESULT_OK) return

        val id =
            data?.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
                ?: return

        setWidget(id)
    }

    private val executor by lazy { Executors.newSingleThreadExecutor() }

    private fun setWidget(id: Int) {
        check(container.isEmpty()) { "Widget already added" }

        val info = appWidgetManager.getAppWidgetInfo(id)
        val view = host.createView(this, id, info)

        if (Build.VERSION.SDK_INT >= 26) view.setExecutor(executor)
        view.setAppWidget(id, info)
        container.addView(view)
    }

    override fun onDestroy() {
        clearWidget()

        runCatching { host.stopListening() }
        runCatching { host.deleteHost() }
        super.onDestroy()
    }

    private fun clearWidget() {
        val childCount = container.childCount
        check(childCount == 1) { "Bad View count: $childCount" }

        val child = container[0]
        container.removeViewAt(0)

        if (child !is AppWidgetHostView) return
        runCatching { host.deleteAppWidgetId(child.appWidgetId) }
    }
}