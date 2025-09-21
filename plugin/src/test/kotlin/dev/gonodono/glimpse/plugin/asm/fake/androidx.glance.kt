@file:Suppress("PackageDirectoryMismatch")

package androidx.glance

internal interface Emittable {
    @Suppress("unused")
    val modifier: GlanceModifier get() = GlanceModifier
}

internal interface GlanceModifier {
    companion object : GlanceModifier
}