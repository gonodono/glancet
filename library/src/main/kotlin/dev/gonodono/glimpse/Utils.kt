package dev.gonodono.glimpse

import androidx.glance.GlanceModifier

internal inline fun <reified T : GlanceModifier> GlanceModifier.find(): T? =
    this.foldIn(null) { accumulator, current -> current as? T ?: accumulator }