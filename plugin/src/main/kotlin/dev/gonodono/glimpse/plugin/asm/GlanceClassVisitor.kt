package dev.gonodono.glimpse.plugin.asm

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import dev.gonodono.glimpse.plugin.GlimpsePluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

internal class GlanceClassVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val modification: MethodModification,
    private val variantName: String,
    private val suppressLogs: Boolean
) : ClassVisitor(api, classVisitor) {

    abstract class Factory : AsmClassVisitorFactory<Factory.Parameters> {

        interface Parameters : InstrumentationParameters {

            @get:Input
            val extension: Property<GlimpsePluginExtension>

            @get:Input
            val variantName: Property<String>
        }

        private val extension get() = parameters.get().extension.get()

        override fun isInstrumentable(classData: ClassData): Boolean =
            Modifications.any { it.isMatch(classData.className, extension) }

        override fun createClassVisitor(
            classContext: ClassContext,
            nextClassVisitor: ClassVisitor
        ): ClassVisitor {
            val clazz = classContext.currentClassData.className
            val modification = Modifications.single { it.isTargetClass(clazz) }

            return GlanceClassVisitor(
                api = instrumentationContext.apiVersion.get(),
                classVisitor = nextClassVisitor,
                modification = modification,
                variantName = parameters.get().variantName.get(),
                suppressLogs = extension.suppressPluginLogs.get()
            )
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String?>?
    ): MethodVisitor? =
        super.visitMethod(access, name, descriptor, signature, exceptions)
            ?.let { modification.visitor(api, it, access, name, descriptor) }

    override fun visitEnd() {
        super.visitEnd()
        if (suppressLogs) return
        println(PluginMessage.format(variantName, modification.featureName))
    }
}

internal const val PluginMessage = "Glimpse has modified %1\$s to enable %2\$s."