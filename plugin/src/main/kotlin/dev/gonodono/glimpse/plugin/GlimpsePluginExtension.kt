package dev.gonodono.glimpse.plugin

import org.gradle.api.provider.Property

/**
 * The settings extension for Glimpse's Gradle plugin.
 */
public interface GlimpsePluginExtension {

    /**
     * The activation flag for the `remoteAdapter` Modifier.
     *
     * The default value is `true`.
     */
    public val remoteAdapter: Property<Boolean>

    /**
     * The activation flag for the `ScrollableLazyColumn` Composable.
     *
     * The default value is `true`.
     */
    public val scrollableLazyColumn: Property<Boolean>

    /**
     * The activation flag for the `ScrollableLazyVerticalGrid` Composable.
     *
     * The default value is `true`.
     */
    public val scrollableLazyVerticalGrid: Property<Boolean>

    /**
     * The suppression flag for the plugin's activity logs.
     *
     * The default value is `false`.
     */
    public val suppressPluginLogs: Property<Boolean>
}