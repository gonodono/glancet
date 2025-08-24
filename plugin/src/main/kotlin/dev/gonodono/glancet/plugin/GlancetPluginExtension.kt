package dev.gonodono.glancet.plugin

import org.gradle.api.provider.Property

/**
 * The settings extension for Glancet's Gradle plugin.
 */
public interface GlancetPluginExtension {

    /**
     * The activation flag for the `remoteAdapter` Modifier.
     *
     * The default value is `true`.
     */
    public val remoteAdapter: Property<Boolean>

    /**
     * The activation flag for the `LazyColumnCompat` Composable.
     *
     * The default value is `true`.
     */
    public val lazyColumnCompat: Property<Boolean>

    /**
     * The activation flag for the `LazyVerticalGridCompat` Composable.
     *
     * The default value is `true`.
     */
    public val lazyVerticalGridCompat: Property<Boolean>

    /**
     * The suppression flag for the plugin's activity logs.
     *
     * The default value is `false`.
     */
    public val suppressPluginLogs: Property<Boolean>
}