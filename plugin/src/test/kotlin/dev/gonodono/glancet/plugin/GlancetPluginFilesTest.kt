package dev.gonodono.glancet.plugin

import org.gradle.api.plugins.ExtensionContainer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class GlancetPluginFilesTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun updateLintTokens() {
        val project = glancetApplicationProject()

        val extensions = project.extensions
        extensions.configurePluginAndCheckLintTokens(
            remoteAdapter = true,
            lazyColumnCompat = true,
            lazyVerticalGridCompat = true
        )
        extensions.configurePluginAndCheckLintTokens(
            remoteAdapter = false,
            lazyColumnCompat = false,
            lazyVerticalGridCompat = false
        )
        extensions.configurePluginAndCheckLintTokens(
            remoteAdapter = false,
            lazyColumnCompat = true,
            lazyVerticalGridCompat = false
        )
    }

    private fun ExtensionContainer.configurePluginAndCheckLintTokens(
        remoteAdapter: Boolean,
        lazyColumnCompat: Boolean,
        lazyVerticalGridCompat: Boolean
    ) {
        if (!remoteAdapter || !lazyColumnCompat || !lazyVerticalGridCompat) {
            this.configure(GlancetPluginExtension::class.java) {
                this@configure.remoteAdapter.set(remoteAdapter)
                this@configure.lazyColumnCompat.set(lazyColumnCompat)
                this@configure.lazyVerticalGridCompat.set(lazyVerticalGridCompat)
            }
        }

        val tokensDir = temporaryFolder.root
        val extension = this.getByType(GlancetPluginExtension::class.java)
        updateLintTokens(tokensDir, TestVariantName, extension)

        fun assertToken(name: String, value: Boolean) {
            val message = "${if (value) "Missing" else "Invalid"} $name token"
            val token = File(tokensDir, "$TestVariantName.$name")
            assertEquals(message, value, token.exists())
        }

        assertToken("remoteAdapter", remoteAdapter)
        assertToken("lazyColumnCompat", lazyColumnCompat)
        assertToken("lazyVerticalGridCompat", lazyVerticalGridCompat)
    }
}