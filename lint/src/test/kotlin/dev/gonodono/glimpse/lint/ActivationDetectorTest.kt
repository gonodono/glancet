package dev.gonodono.glimpse.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class ActivationDetectorTest : GlimpseLintTest() {

    override fun getDetector(): Detector = ActivationDetector()

    override fun getIssues(): List<Issue>? =
        listOf(ActivationDetector.GlimpseFeatureNotActivated)

    @Test
    fun `features are all activated`() =
        runTest(
            testFileContent =
                """
                @Composable
                fun Test() {
                    AndroidRemoteViews(GlanceModifier.remoteAdapter())
                    ScrollableLazyColumn()
                    ScrollableLazyVerticalGrid()
                }
                """.trimIndent(),
            TestFiles.file()
                .to("outputs/glimpse/test.remoteAdapter")
                .withSource("active")
                .within("build"),
            TestFiles.file()
                .to("outputs/glimpse/test.scrollableLazyColumn")
                .withSource("active")
                .within("build"),
            TestFiles.file()
                .to("outputs/glimpse/test.scrollableLazyVerticalGrid")
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
                    ScrollableLazyColumn()
                    ScrollableLazyVerticalGrid()
                }
                """.trimIndent(),
            expectedText =
                """
                src/test.kt:13: Error: Glimpse features must be activated with the plugin. Be sure to rebuild after adding or updating it. [GlimpseFeatureNotActivated]
                    AndroidRemoteViews(GlanceModifier.remoteAdapter())
                                                      ~~~~~~~~~~~~~
                src/test.kt:14: Error: Glimpse features must be activated with the plugin. Be sure to rebuild after adding or updating it. [GlimpseFeatureNotActivated]
                    ScrollableLazyColumn()
                    ~~~~~~~~~~~~~~~~~~~~
                src/test.kt:15: Error: Glimpse features must be activated with the plugin. Be sure to rebuild after adding or updating it. [GlimpseFeatureNotActivated]
                    ScrollableLazyVerticalGrid()
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~
                3 errors
                """.trimIndent()
        )
}