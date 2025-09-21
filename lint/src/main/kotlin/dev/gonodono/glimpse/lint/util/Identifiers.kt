package dev.gonodono.glimpse.lint.util

internal const val ComposableFqcn = "androidx.compose.runtime.Composable"
internal const val UnitFqcn = "kotlin.Unit"

internal data class Function(
    val name: String,
    val fullyQualifiedClassName: String
)

private infix fun String.from(fqcn: String): Function = Function(this, fqcn)

internal val RemoteAdapter =
    "remoteAdapter" from
            "dev.gonodono.glimpse.remoteadapter.RemoteAdapterModifierKt"

internal val ScrollableLazyColumn =
    "ScrollableLazyColumn" from
            "dev.gonodono.glimpse.scrollablelazy.ScrollableLazyColumnKt"

internal val ScrollableLazyVerticalGrid =
    "ScrollableLazyVerticalGrid" from
            "dev.gonodono.glimpse.scrollablelazy.ScrollableLazyVerticalGridKt"

internal val ActivationTargets =
    listOf(RemoteAdapter, ScrollableLazyColumn, ScrollableLazyVerticalGrid)

internal val AndroidRemoteViews =
    "AndroidRemoteViews" from
            "androidx.glance.appwidget.AndroidRemoteViewsKt"