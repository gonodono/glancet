package dev.gonodono.glancet.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

internal class GlancetClassVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val modification: Modification,
    private val variantName: String,
    private val suppressLogs: Boolean
) : ClassVisitor(api, classVisitor) {

    abstract class Factory : AsmClassVisitorFactory<Factory.Parameters> {

        interface Parameters : InstrumentationParameters {

            @get:Input
            val extension: Property<GlancetPluginExtension>

            @get:Input
            val variantName: Property<String>
        }

        override fun isInstrumentable(classData: ClassData): Boolean =
            Modifications.any { it.isMatch(classData.className, extension) }

        override fun createClassVisitor(
            classContext: ClassContext,
            nextClassVisitor: ClassVisitor
        ): ClassVisitor {
            val clazz = classContext.currentClassData.className
            val modification = Modifications.single { it.isTargetClass(clazz) }
            return GlancetClassVisitor(
                api = instrumentationContext.apiVersion.get(),
                classVisitor = nextClassVisitor,
                modification = modification,
                variantName = parameters.get().variantName.get(),
                suppressLogs = extension.suppressPluginLogs.get()
            )
        }

        private val extension: GlancetPluginExtension
            get() = parameters.get().extension.get()
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String?>?
    ): MethodVisitor? {
        val methodVisitor =
            super.visitMethod(access, name, descriptor, signature, exceptions)
                ?: return null

        return if (modification.isTargetMethod(name, descriptor)) {
            modification.methodVisitorFactory
                .createVisitor(api, methodVisitor, access, name, descriptor)
        } else {
            methodVisitor
        }
    }

    override fun visitEnd() {
        super.visitEnd()

        if (suppressLogs) return
        println(
            "Glancet has modified $variantName " +
                    "to enable ${modification.featureName}."
        )
    }
}