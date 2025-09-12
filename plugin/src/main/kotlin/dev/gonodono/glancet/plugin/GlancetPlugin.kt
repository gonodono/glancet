package dev.gonodono.glancet.plugin

import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import dev.gonodono.glancet.plugin.asm.GlanceClassVisitor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.internal.extensions.stdlib.capitalized
import java.io.File
import java.io.FileNotFoundException

/**
 * The Gradle plugin for the Glancet library. This plugin handles the Glance
 * bytecode modifications necessary to inject the library's custom behavior.
 */
public class GlancetPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.checkIsAndroidApplicationOrLibrary()

        val extension =
            project.extensions
                .create("glancet", GlancetPluginExtension::class.java)
                .apply {
                    remoteAdapter.convention(true)
                    lazyColumnCompat.convention(true)
                    lazyVerticalGridCompat.convention(true)
                    suppressPluginLogs.convention(false)
                }

        project.configureTransformations(extension)
    }
}

internal fun Project.checkIsAndroidApplicationOrLibrary() {
    if (this.pluginManager.hasPlugin("com.android.application")) return
    if (this.pluginManager.hasPlugin("com.android.library")) return
    error(MissingAndroidPluginErrorMessage)
}

internal const val MissingAndroidPluginErrorMessage =
    "The Glancet plugin can be used only in an Android application or library."

internal fun Project.configureTransformations(extension: GlancetPluginExtension) =
    this.extensions
        .getByType(AndroidComponentsExtension::class.java)
        .onVariants { variant ->

            variant.instrumentation.transformClassesWith(
                GlanceClassVisitor.Factory::class.java,
                InstrumentationScope.ALL
            ) { parameters ->
                parameters.extension.set(extension)
                parameters.variantName.set(variant.name)
            }

            // Not currently necessary; no stack increase or branch or anything.
            // variant.instrumentation.setAsmFramesComputationMode(
            //     FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            // )

            // Must wait until AGP creates the variants' assemble tasks.
            this.afterEvaluate { configureLintTokensTask(variant.name) }
        }

internal fun Project.configureLintTokensTask(variantName: String) {
    val suffix = variantName.capitalized()

    val extension =
        extensions.getByType(GlancetPluginExtension::class.java)

    val tokensTask =
        tasks.register("updateGlancetLintTokens$suffix") {
            val buildDir = project.layout.buildDirectory
            val tokensDir = buildDir.dir(LintTokensDirectory).get().asFile
            doLast { updateLintTokens(tokensDir, variantName, extension) }
        }

    val assembleTask = project.tasks.getByName("assemble$suffix")
    tokensTask.configure { dependsOn(assembleTask) }
    assembleTask.finalizedBy(tokensTask)
}

internal fun updateLintTokens(
    directory: File,
    variantName: String,
    extension: GlancetPluginExtension
) {
    directory.mkdirs()

    fun updateToken(featureName: String, activated: Property<Boolean>) {
        val file = File(directory, "$variantName.$featureName")
        if (activated.get()) file.writeText("active") else file.deleteIfExists()
    }

    updateToken("remoteAdapter", extension.remoteAdapter)
    updateToken("lazyColumnCompat", extension.lazyColumnCompat)
    updateToken("lazyVerticalGridCompat", extension.lazyVerticalGridCompat)
}

private fun File.deleteIfExists() =
    try {
        this.delete()
    } catch (_: FileNotFoundException) {
        // ignore
    }

internal const val LintTokensDirectory = "outputs/glancet/"