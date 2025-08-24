package dev.gonodono.glancet.plugin

import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.impl.capitalizeFirstChar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import java.io.FileNotFoundException

/**
 * The Gradle plugin for the Glancet library. This plugin handles the Glance
 * bytecode modifications necessary to inject the library's custom behavior.
 */
public class GlancetPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.checkIsAndroidAppOrLibrary()

        val extension =
            project.extensions
                .create("glancet", GlancetPluginExtension::class.java)
                .apply {
                    remoteAdapter.convention(true)
                    lazyColumnCompat.convention(true)
                    lazyVerticalGridCompat.convention(true)
                    suppressPluginLogs.convention(false)
                }

        project.applyGlancetPlugin(extension)
    }
}

private fun Project.checkIsAndroidAppOrLibrary() {
    if (this.pluginManager.hasPlugin("com.android.application")) return
    if (this.pluginManager.hasPlugin("com.android.library")) return
    error("The Glancet plugin can be used only with an Android app or library.")
}

private fun Project.applyGlancetPlugin(extension: GlancetPluginExtension) =
    this.extensions
        .getByType(AndroidComponentsExtension::class.java)
        .onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                GlancetClassVisitor.Factory::class.java,
                InstrumentationScope.ALL
            ) { parameters ->
                parameters.extension.set(extension)
                parameters.variantName.set(variant.name)
            }

            this.configureLintSignalTask(variant.name)
        }

private fun Project.configureLintSignalTask(variantName: String) =
    this.afterEvaluate {
        val suffix = variantName.capitalizeFirstChar()

        val glancet =
            this.extensions.getByType(GlancetPluginExtension::class.java)

        val task =
            this.tasks.register("generateLintSignal$suffix") {
                val directory = layout.buildDirectory.dir(LintSignalDirectory)
                doLast { updateSignalFiles(glancet, directory, variantName) }
            }

        this.tasks.getByName("assemble$suffix").finalizedBy(task)
    }

private fun updateSignalFiles(
    glancet: GlancetPluginExtension,
    directory: Provider<Directory>,
    variantName: String
) {
    val dir = directory.get().asFile.apply { mkdirs() }

    fun updateSignal(featureName: String, activated: Property<Boolean>) {
        val file = File(dir, "$variantName.$featureName")
        if (activated.get()) file.writeText("active") else file.deleteIfExists()
    }

    updateSignal("remoteAdapter", glancet.remoteAdapter)
    updateSignal("lazyColumnCompat", glancet.lazyColumnCompat)
    updateSignal("lazyVerticalGridCompat", glancet.lazyVerticalGridCompat)
}

private fun File.deleteIfExists() =
    try {
        this.delete()
    } catch (_: FileNotFoundException) {
        // ignore
    }

private const val LintSignalDirectory = "outputs/glancet/"