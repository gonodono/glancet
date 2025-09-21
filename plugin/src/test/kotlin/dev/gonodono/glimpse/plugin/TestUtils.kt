package dev.gonodono.glimpse.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

// TODO: "Test events were not received" if the plugin is used in other modules.

internal const val TestVariantName = "test"

internal fun Project.applyAndroidApplicationPlugin() =
    this.pluginManager.apply("com.android.application")

internal fun Project.applyAndroidLibraryPlugin() =
    this.pluginManager.apply("com.android.library")

internal fun Project.applyGlimpsePlugin() =
    this.pluginManager.apply(GlimpsePlugin::class.java)

internal fun defaultProject(): Project =
    ProjectBuilder.builder().build()

internal fun glimpseApplicationProject(): Project =
    defaultProject().also { project ->
        project.applyAndroidApplicationPlugin()
        project.applyGlimpsePlugin()
    }