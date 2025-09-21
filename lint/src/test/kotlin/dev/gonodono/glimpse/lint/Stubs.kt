package dev.gonodono.glimpse.lint

import com.android.tools.lint.checks.infrastructure.TestFiles

// TODO: Remove @Suppress("Annotator")s when fixed in K2.

internal val ComposableStub =
    TestFiles.kotlin(
        "androidx/compose/runtime/Composable.kt",
        @Suppress("Annotator")
        """
        package androidx.compose.runtime
        annotation class Composable
        """
            .trimIndent()
    )
        .within("src")

internal val GlanceModifierStub =
    TestFiles.kotlin(
        "androidx/glance/GlanceModifier.kt",
        @Suppress("Annotator")
        """
        package androidx.glance
        interface GlanceModifier {
            companion object : GlanceModifier
        }
        """
            .trimIndent()
    )
        .within("src")

internal val ScrollableLazyColumnStub =
    TestFiles.kotlin(
        "dev/gonodono/glimpse/scrollablelazy/ScrollableLazyColumn.kt",
        @Suppress("Annotator")
        """
        package dev.gonodono.glimpse.scrollablelazy
        import androidx.compose.runtime.Composable
        @Composable
        fun ScrollableLazyColumn(modifier: GlanceModifier = GlanceModifier)
        """
            .trimIndent()
    )
        .within("src")

internal val ScrollableLazyVerticalGridStub =
    TestFiles.kotlin(
        "dev/gonodono/glimpse/scrollablelazy/ScrollableLazyVerticalGrid.kt",
        @Suppress("Annotator")
        """
        package dev.gonodono.glimpse.scrollablelazy
        import androidx.compose.runtime.Composable
        @Composable
        fun ScrollableLazyVerticalGrid(modifier: GlanceModifier = GlanceModifier)
        """
            .trimIndent()
    )
        .within("src")

internal val AndroidRemoteViewsStub =
    TestFiles.kotlin(
        "androidx/glance/appwidget/AndroidRemoteViews.kt",
        @Suppress("Annotator")
        """
        package androidx.glance.appwidget
        import androidx.compose.runtime.Composable
        @Composable
        fun AndroidRemoteViews(modifier: GlanceModifier = GlanceModifier)
        """
            .trimIndent()
    )
        .within("src")

internal val RemoteAdapterStub =
    TestFiles.kotlin(
        "dev/gonodono/glimpse/remoteadapter/RemoteAdapterModifier.kt",
        @Suppress("Annotator")
        """
        package dev.gonodono.glimpse.remoteadapter
        import androidx.glance.GlanceModifier
        fun GlanceModifier.remoteAdapter(): GlanceModifier = this
        """
            .trimIndent()
    )
        .within("src")

internal val DummyComposableStub =
    TestFiles.kotlin(
        "test/pkg/Utils.kt",
        @Suppress("Annotator")
        """
        package test.pkg
        import androidx.glance.GlanceModifier
        import androidx.compose.runtime.Composable
        fun GlanceModifier.extension1(): GlanceModifier = this
        fun GlanceModifier.extension2(): GlanceModifier = this
        @Composable
        fun DummyComposable(modifier: GlanceModifier =  GlanceModifier) {}
        fun DummyComposable2(
            modifier: GlanceModifier =  GlanceModifier,
            content: @Composable () -> Unit = {}
        ) {}
        """
            .trimIndent()
    )
        .within("src")