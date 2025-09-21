package dev.gonodono.glimpse.plugin.asm

import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationContext
import com.android.build.gradle.internal.instrumentation.ASM_API_VERSION
import com.android.build.gradle.internal.instrumentation.ClassDataImpl
import dev.gonodono.glimpse.plugin.GlimpsePluginExtension
import dev.gonodono.glimpse.plugin.TestVariantName
import dev.gonodono.glimpse.plugin.glimpseApplicationProject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

@RunWith(JUnit4::class)
class GlanceClassVisitorTest {

    @Test
    fun factoryIsInstrumentable() {
        val project = glimpseApplicationProject()
        val extensions = project.extensions

        val extension = extensions.getByType(GlimpsePluginExtension::class.java)
        val parameters = TestFactoryParameters(project.objects, extension)
        val factory = TestGlanceClassVisitorFactory(project.objects, parameters)

        Modifications.forEach { modification ->
            val fqcn = modification.targetClassFullyQualifiedName
            assertTrue(
                "$fqcn is not instrumentable",
                factory.isInstrumentable(fqcn.toClassData())
            )
        }
    }

    @Test
    fun factoryCreateClassVisitor() {
        val project = glimpseApplicationProject()
        val extensions = project.extensions

        val extension = extensions.getByType(GlimpsePluginExtension::class.java)
        val parameters = TestFactoryParameters(project.objects, extension)
        val factory = TestGlanceClassVisitorFactory(project.objects, parameters)
        val nextClassVisitor = object : ClassVisitor(Opcodes.ASM9) {}

        Modifications.forEach { modification ->
            val fqcn = modification.targetClassFullyQualifiedName
            val context = object : ClassContext {
                override val currentClassData: ClassData = fqcn.toClassData()
                override fun loadClassData(className: String): ClassData? =
                    currentClassData
            }
            assertNotNull(
                "Class visitor is null for fqcn",
                factory.createClassVisitor(context, nextClassVisitor)
            )
        }
    }
}

private fun String.toClassData(): ClassData =
    ClassDataImpl(this, emptyList(), emptyList(), emptyList())

private class TestFactoryParameters(
    objectFactory: ObjectFactory,
    pluginExtension: GlimpsePluginExtension
) : GlanceClassVisitor.Factory.Parameters {

    override val extension: Property<GlimpsePluginExtension> =
        objectFactory.property(GlimpsePluginExtension::class.java)
            .convention(pluginExtension)

    override val variantName: Property<String> =
        objectFactory.property(String::class.java)
            .convention(TestVariantName)
}

private class TestGlanceClassVisitorFactory(
    objectFactory: ObjectFactory,
    factoryParameters: Parameters
) : GlanceClassVisitor.Factory() {

    override val parameters: Property<Parameters> =
        objectFactory.property(Parameters::class.java)
            .convention(factoryParameters)

    override val instrumentationContext: InstrumentationContext =
        object : InstrumentationContext {
            override val apiVersion: Property<Int> =
                objectFactory.property(Int::class.java)
                    .convention(ASM_API_VERSION)
        }
}