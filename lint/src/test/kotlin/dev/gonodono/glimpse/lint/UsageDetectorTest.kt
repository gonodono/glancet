package dev.gonodono.glimpse.lint

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class UsageDetectorTest : GlimpseLintTest() {

    override fun getDetector(): Detector = UsageDetector()

    override fun getIssues(): List<Issue>? =
        listOf(UsageDetector.RemoteAdapterInvalidComposable)

    @Test
    fun `remoteAdapter, valid usage`() =
        runTest(
            testFileContent =
                """
                @Composable
                fun Test() {
                    AndroidRemoteViews(GlanceModifier.remoteAdapter())
                    AndroidRemoteViews(
                        GlanceModifier
                            .extension1()
                            .remoteAdapter()
                            .extension2()
                    )
                    DummyComposable(
                        GlanceModifier.also { GlanceModifier.remoteAdapter() }
                    )
                    // Valid for now; may be fixed in the future.
                    val modifier = GlanceModifier.remoteAdapter()
                    DummyComposable(modifier)
                }
                """
                    .trimIndent()
        )

    @Test
    fun `remoteAdapter, invalid Composable`() =
        runTest(
            testFileContent =
                """
                @Composable
                fun Test() {
                    DummyComposable(GlanceModifier.remoteAdapter())
                    DummyComposable(
                        GlanceModifier
                            .extension1()
                            .remoteAdapter()
                            .extension2()
                    )
                }
                """
                    .trimIndent(),
            expectedText =
                """
                src/test.kt:13: Error: remoteAdapter works only with AndroidRemoteViews. [RemoteAdapterInvalidComposable]
                    DummyComposable(GlanceModifier.remoteAdapter())
                                                   ~~~~~~~~~~~~~
                src/test.kt:17: Error: remoteAdapter works only with AndroidRemoteViews. [RemoteAdapterInvalidComposable]
                            .remoteAdapter()
                             ~~~~~~~~~~~~~
                2 errors
                """
                    .trimIndent()
        )
}