package dev.gonodono.glancet

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.semantics.semantics
import androidx.glance.semantics.testTag
import androidx.glance.testing.GlanceNodeMatcher
import androidx.glance.testing.unit.hasTestTag
import androidx.test.core.app.ApplicationProvider

internal inline val ApplicationContext: Context
    get() = ApplicationProvider.getApplicationContext()

internal inline fun <T : GlanceModifier> assertModifierPresent(
    modifierClazz: Class<T>,
    crossinline block: @Composable (taggedModifier: GlanceModifier) -> Unit
) {
    runGlanceAppWidgetUnitTest {
        provideComposable {
            block(GlanceModifier.semantics { testTag = GlancetTestTag })
        }
        onNode(hasTestTag(GlancetTestTag)).assert(
            GlanceNodeMatcher("hasLazyCompatModifier") { node ->
                node.value.emittable.modifier.any { modifier ->
                    modifierClazz.isAssignableFrom(modifier.javaClass)
                }
            }
        )
    }
}

private const val GlancetTestTag = "GlancetTestTag"