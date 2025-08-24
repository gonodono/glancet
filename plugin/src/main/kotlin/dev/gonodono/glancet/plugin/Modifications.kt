@file:Suppress("SpellCheckingInspection")

package dev.gonodono.glancet.plugin

import org.gradle.api.provider.Property
import org.objectweb.asm.MethodVisitor

internal data class Modification(
    val featureName: String,
    private val featureSwitch: GlancetPluginExtension.() -> Property<Boolean>,
    val methodVisitorFactory: MethodVisitorFactory,
    private val targetClassFullyQualifiedName: String,
    private val targetMethodBytecodeName: String,
    private val targetMethodBytecodeDescriptor: String
) {
    fun isMatch(className: String, extension: GlancetPluginExtension): Boolean =
        isTargetClass(className) && featureSwitch(extension).get()

    fun isTargetClass(className: String): Boolean =
        targetClassFullyQualifiedName == className

    fun isTargetMethod(name: String?, descriptor: String?): Boolean =
        targetMethodBytecodeName == name &&
                targetMethodBytecodeDescriptor == descriptor
}

private val RemoteAdapterModification =
    Modification(
        featureName =
            "remoteAdapter",
        featureSwitch =
            GlancetPluginExtension::remoteAdapter,
        methodVisitorFactory =
            ::RemoteAdapterAdvice,
        targetClassFullyQualifiedName =
            "androidx.glance.appwidget.RemoteViewsTranslatorKt",
        targetMethodBytecodeName =
            "translateEmittableAndroidRemoteViews",
        targetMethodBytecodeDescriptor =
            "(Landroid/widget/RemoteViews;" +
                    "Landroidx/glance/appwidget/TranslationContext;" +
                    "Landroidx/glance/appwidget/EmittableAndroidRemoteViews;" +
                    ")V"
    )

private class RemoteAdapterAdvice(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?
) : AfterAdvice(api, methodVisitor, access, name, descriptor) {

    override fun afterMethod() {
        // Landroid/widget/RemoteViews;
        visitVarInsn(ALOAD, 0)

        // Landroidx/glance/appwidget/EmittableAndroidRemoteViews;
        visitVarInsn(ALOAD, 2)
        visitTypeInsn(CHECKCAST, "androidx/glance/Emittable")

        visitMethodInsn(
            /* opcodeAndSource = */
            INVOKESTATIC,
            /* owner = */
            "dev/gonodono/glancet/remoteadapter/RemoteAdapterModifierKt",
            /* name = */
            "setRemoteAdapterIfPresent",
            /* descriptor = */
            "(Landroid/widget/RemoteViews;" +
                    "Landroidx/glance/Emittable;" +
                    ")V",
            /* isInterface = */
            false
        )
    }
}

private val LazyColumnCompatModification =
    Modification(
        featureName =
            "lazyColumnCompat",
        featureSwitch =
            GlancetPluginExtension::lazyColumnCompat,
        methodVisitorFactory =
            ::LazyCompatAdvice,
        targetClassFullyQualifiedName =
            "androidx.glance.appwidget.translators.LazyListTranslatorKt",
        targetMethodBytecodeName =
            "translateEmittableLazyList",
        targetMethodBytecodeDescriptor =
            "(Landroid/widget/RemoteViews;" +
                    "Landroidx/glance/appwidget/TranslationContext;" +
                    "Landroidx/glance/appwidget/lazy/EmittableLazyList;" +
                    "Landroidx/glance/appwidget/InsertedViewInfo;" +
                    ")V"
    )

private val LazyVerticalGridCompatModification =
    Modification(
        featureName =
            "lazyVerticalGridCompat",
        featureSwitch =
            GlancetPluginExtension::lazyVerticalGridCompat,
        methodVisitorFactory =
            ::LazyCompatAdvice,
        targetClassFullyQualifiedName =
            "androidx.glance.appwidget.translators.LazyVerticalGridTranslatorKt",
        targetMethodBytecodeName =
            "translateEmittableLazyVerticalGrid",
        targetMethodBytecodeDescriptor =
            "(Landroid/widget/RemoteViews;" +
                    "Landroidx/glance/appwidget/TranslationContext;" +
                    "Landroidx/glance/appwidget/lazy/EmittableLazyVerticalGrid;" +
                    "Landroidx/glance/appwidget/InsertedViewInfo;" +
                    ")V"
    )

private class LazyCompatAdvice(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?
) : AfterAdvice(api, methodVisitor, access, name, descriptor) {

    override fun afterMethod() {
        // Landroid/widget/RemoteViews;
        visitVarInsn(ALOAD, 0)

        // Landroidx/glance/appwidget/EmittableAndroidRemoteViews;
        visitVarInsn(ALOAD, 2)
        visitTypeInsn(CHECKCAST, "androidx/glance/Emittable")

        // Ldev/gonodono/glancet/lazycompat/TranslationContext;
        visitVarInsn(ALOAD, 3)
        visitMethodInsn(
            /* opcodeAndSource = */ INVOKEVIRTUAL,
            /* owner = */ "androidx/glance/appwidget/InsertedViewInfo",
            /* name = */ "getMainViewId",
            /* descriptor = */ "()I",
            /* isInterface = */ false
        )

        visitMethodInsn(
            /* opcodeAndSource = */
            INVOKESTATIC,
            /* owner = */
            "dev/gonodono/glancet/lazycompat/LazyCompatModifierKt",
            /* name = */
            "applyLazyCompatIfPresent",
            /* descriptor = */
            "(Landroid/widget/RemoteViews;" +
                    "Landroidx/glance/Emittable;" +
                    "I" +
                    ")V",
            /* isInterface = */
            false
        )
    }
}

internal val Modifications: Set<Modification> =
    setOf(
        RemoteAdapterModification,
        LazyColumnCompatModification,
        LazyVerticalGridCompatModification
    )