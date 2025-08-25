package dev.gonodono.glancet.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

internal fun interface MethodVisitorFactory {

    fun createVisitor(
        api: Int,
        methodVisitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ): MethodVisitor
}

internal abstract class AfterAdvice(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
) : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

    final override fun onMethodExit(opcode: Int) {
        if (opcode == RETURN) afterMethod()
    }

    abstract fun afterMethod()
}