package dev.gonodono.glimpse.demo.internal

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import dev.gonodono.glimpse.demo.R

// Currently unused; preserved for future testing, etc.

class RemoteAdapterWidgetService : RemoteViewsService() {

    companion object {

        @Suppress("unused")
        fun createIntent(
            context: Context,
            appWidgetId: Int,
            itemLayoutId: Int
        ): Intent =
            Intent(context, RemoteAdapterWidgetService::class.java)
                .apply { appWidgetIdExtra = appWidgetId }
                .putExtra(ExtraItemLayoutId, itemLayoutId)
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val appWidgetId = intent.appWidgetIdExtra
        val itemLayoutId = intent.getIntExtra(ExtraItemLayoutId, 0)
        return RemoteAdapterWidgetFactory(this, appWidgetId, itemLayoutId)
    }
}

private class RemoteAdapterWidgetFactory(
    private val context: Context,
    private val appWidgetId: Int,
    private val itemLayoutId: Int
) : RemoteViewsService.RemoteViewsFactory {

    override fun getCount(): Int = ItemCount

    override fun getViewAt(position: Int): RemoteViews? =
        RemoteViews(context.packageName, itemLayoutId).apply {
            val click = Intent()
                .apply { appWidgetIdExtra = appWidgetId }
                .putExtra(ExtraItemPosition, position)
            setOnClickFillInIntent(R.id.text, click)

            setTextViewText(R.id.text, "#$position")
        }

    override fun onCreate() {}
    override fun onDataSetChanged() {}
    override fun onDestroy() {}
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}