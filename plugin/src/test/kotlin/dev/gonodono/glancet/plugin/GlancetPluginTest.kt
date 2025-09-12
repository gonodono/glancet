package dev.gonodono.glancet.plugin

import dev.gonodono.glancet.plugin.asm.PluginMessage
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class GlancetPluginTest {

    @Ignore("Run manually")
    @Test
    fun buildOutput() {
        // Kinda cheap and hacky, but it avoids a lot of redundancy.
        val testsDir = File(System.getProperty("tests.directory"))
        assertTrue("tests directory not found", testsDir.exists())

        val result =
            GradleRunner.create()
                .withProjectDir(testsDir)
                .withArguments("assembleDebug")
                .build()

        fun assertMessage(name: String) {
            val output = result.output
            assertTrue(
                "Missing $name output.",
                output.contains(PluginMessage.format("debug", name))
            )
        }

        assertMessage("remoteAdapter")
        assertMessage("lazyColumnCompat")
        assertMessage("lazyVerticalGridCompat")
    }

    @Test
    fun checkIsAndroidApplicationOrLibrary() {
        defaultProject()
            .also { it.applyAndroidApplicationPlugin() }
            .checkIsAndroidApplicationOrLibrary()

        defaultProject()
            .also { it.applyAndroidLibraryPlugin() }
            .checkIsAndroidApplicationOrLibrary()

        val exception =
            assertThrows(IllegalStateException::class.java) {
                defaultProject().checkIsAndroidApplicationOrLibrary()
            }
        assertEquals(exception.message, MissingAndroidPluginErrorMessage)
    }

    @Test
    fun configureLintTokensTask() {
        val project = glancetApplicationProject()
        val suffix = TestVariantName.capitalized()
        val tasks = project.tasks

        val assembleTask = tasks.register("assemble$suffix").get()
        project.configureLintTokensTask(TestVariantName)

        val tokensTask = tasks.named("updateGlancetLintTokens$suffix").get()
        assertTrue(
            "Tokens task does not depend on assemble",
            tokensTask.dependsOn.contains(assembleTask)
        )

        val finalizers = assembleTask.run { finalizedBy.getDependencies(this) }
        assertTrue(
            "Assemble task is not finalized by tokens",
            finalizers.contains(tokensTask)
        )
    }
}