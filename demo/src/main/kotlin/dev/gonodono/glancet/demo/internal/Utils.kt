package dev.gonodono.glancet.demo.internal

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dev.gonodono.glancet.demo.BuildConfig

// Constants

internal const val ItemCount = 50

internal val Int.isEven: Boolean get() = this % 2 == 0


// Logs

private const val Tag = "Glancet"

internal fun debugLog(msg: String) {
    if (BuildConfig.DEBUG) Log.d(Tag, msg)
}

internal fun <T : GlanceAppWidgetReceiver> Class<T>.displayName(): String =
    this.simpleName.substringBeforeLast("WidgetReceiver")


// App widgets

internal inline val Context.appWidgetManager: AppWidgetManager
    get() = AppWidgetManager.getInstance(this)

internal fun Context.canPinAppWidgets(): Boolean =
    Build.VERSION.SDK_INT >= 26 &&
            appWidgetManager.isRequestPinAppWidgetSupported


// Intents

internal const val ActionItemClick =
    "${BuildConfig.APPLICATION_ID}.action.ITEM_CLICK"

internal const val ExtraItemLayoutId =
    "${BuildConfig.APPLICATION_ID}.extra.ITEM_LAYOUT_ID"

internal const val ExtraItemPosition =
    "${BuildConfig.APPLICATION_ID}.extra.ITEM_POSITION"

internal var Intent.appWidgetIdExtra: Int
    get() = getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
    set(value) {
        putExtra(EXTRA_APPWIDGET_ID, value)
    }

internal fun Intent.setDataToSelfUri(): Intent =
    this.setData(this.toUri(Intent.URI_INTENT_SCHEME).toUri())


// PackageManager

internal fun <T> Class<T>.isComponentEnabled(context: Context): Boolean
        where T : GlanceAppWidgetReceiver {

    val manager = context.packageManager
    val name = ComponentName(context, this)
    val setting = manager.getComponentEnabledSetting(name)

    return when (setting) {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true

        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ->
            manager.getReceiverInfo(name, ReceiverFlags).isEnabled

        else -> false
    }
}

private val ReceiverFlags =
    if (Build.VERSION.SDK_INT >= 24) {
        PackageManager.MATCH_DISABLED_COMPONENTS
    } else {
        @Suppress("DEPRECATION")
        PackageManager.GET_DISABLED_COMPONENTS
    }