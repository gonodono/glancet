package dev.gonodono.glancet.lint.util

internal const val ComposableFqcn = "androidx.compose.runtime.Composable"
internal const val UnitFqcn = "kotlin.Unit"

internal data class Function(
    val name: String,
    val fullyQualifiedClassName: String
)

private infix fun String.from(fqcn: String): Function = Function(this, fqcn)

internal val RemoteAdapter =
    "remoteAdapter" from
            "dev.gonodono.glancet.remoteadapter.RemoteAdapterModifierKt"

internal val LazyColumnCompat =
    "LazyColumnCompat" from
            "dev.gonodono.glancet.lazycompat.LazyColumnCompatKt"

internal val LazyVerticalGridCompat =
    "LazyVerticalGridCompat" from
            "dev.gonodono.glancet.lazycompat.LazyVerticalGridCompatKt"

internal val ActivationTargets =
    listOf(RemoteAdapter, LazyColumnCompat, LazyVerticalGridCompat)

internal val AndroidRemoteViews =
    "AndroidRemoteViews" from
            "androidx.glance.appwidget.AndroidRemoteViewsKt"