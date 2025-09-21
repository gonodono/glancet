package dev.gonodono.glimpse.plugin

import org.gradle.api.plugins.ExtensionContainer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class GlimpsePluginFilesTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun updateLintTokens() {
        val project = glimpseApplicationProject()

        val extensions = project.extensions
        extensions.configurePluginAndCheckLintTokens(
            remoteAdapter = true,
            scrollableLazyColumn = true,
            scrollableLazyVerticalGrid = true
        )
        extensions.configurePluginAndCheckLintTokens(
            remoteAdapter = false,
            scrollableLazyColumn = false,
            scrollableLazyVerticalGrid = false
        )
        extensions.configurePluginAndCheckLintTokens(
            remoteAdapter = false,
            scrollableLazyColumn = true,
            scrollableLazyVerticalGrid = false
        )
    }

    private fun ExtensionContainer.configurePluginAndCheckLintTokens(
        remoteAdapter: Boolean,
        scrollableLazyColumn: Boolean,
        scrollableLazyVerticalGrid: Boolean
    ) {
        if (!remoteAdapter || !scrollableLazyColumn || !scrollableLazyVerticalGrid) {
            this.configure(GlimpsePluginExtension::class.java) {
                this@configure.remoteAdapter.set(remoteAdapter)
                this@configure.scrollableLazyColumn.set(scrollableLazyColumn)
                this@configure.scrollableLazyVerticalGrid.set(scrollableLazyVerticalGrid)
            }
        }

        val tokensDir = temporaryFolder.root
        val extension = this.getByType(GlimpsePluginExtension::class.java)
        updateLintTokens(tokensDir, TestVariantName, extension)

        fun assertToken(name: String, value: Boolean) {
            val message = "${if (value) "Missing" else "Invalid"} $name token"
            val token = File(tokensDir, "$TestVariantName.$name")
            assertEquals(message, value, token.exists())
        }

        assertToken("remoteAdapter", remoteAdapter)
        assertToken("scrollableLazyColumn", scrollableLazyColumn)
        assertToken("scrollableLazyVerticalGrid", scrollableLazyVerticalGrid)
    }
}