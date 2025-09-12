package dev.gonodono.glancet.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class ActivationDetectorTest : GlancetLintTest() {

    override fun getDetector(): Detector = ActivationDetector()

    override fun getIssues(): List<Issue>? =
        listOf(ActivationDetector.GlancetFeatureNotActivated)

    @Test
    fun `features are all activated`() =
        runTest(
            testFileContent =
                """
                @Composable
                fun Test() {
                    AndroidRemoteViews(GlanceModifier.remoteAdapter())
                    LazyColumnCompat()
                    LazyVerticalGridCompat()
                }
                """.trimIndent(),
            TestFiles.file()
                .to("outputs/glancet/test.remoteAdapter")
                .withSource("active")
                .within("build"),
            TestFiles.file()
                .to("outputs/glancet/test.lazyColumnCompat")
                .withSource("active")
                .within("build"),
            TestFiles.file()
                .to("outputs/glancet/test.lazyVerticalGridCompat")
                .withSource("active")
                .within("build")
        )

    @Test
    fun `features are not activated`() =
        runTest(
            testFileContent =
                """
                @Composable
                fun Test() {
                    AndroidRemoteViews(GlanceModifier.remoteAdapter())
                    LazyColumnCompat()
                    LazyVerticalGridCompat()
                }
                """.trimIndent(),
            expectedText =
                """
                src/test.kt:13: Error: Glancet features must be activated with the plugin. Be sure to rebuild after adding or updating it. [GlancetFeatureNotActivated]
                    AndroidRemoteViews(GlanceModifier.remoteAdapter())
                                                      ~~~~~~~~~~~~~
                src/test.kt:14: Error: Glancet features must be activated with the plugin. Be sure to rebuild after adding or updating it. [GlancetFeatureNotActivated]
                    LazyColumnCompat()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:15: Error: Glancet features must be activated with the plugin. Be sure to rebuild after adding or updating it. [GlancetFeatureNotActivated]
                    LazyVerticalGridCompat()
                    ~~~~~~~~~~~~~~~~~~~~~~
                3 errors
                """.trimIndent()
        )
}