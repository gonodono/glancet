@file:Suppress("SpellCheckingInspection")

package dev.gonodono.glancet.plugin.asm

import dev.gonodono.glancet.plugin.GlancetPluginExtension
import org.gradle.api.provider.Property
import org.objectweb.asm.MethodVisitor

internal data class MethodModification(
    val featureName: String,
    val featureSwitch: GlancetPluginExtension.() -> Property<Boolean>,
    val createMethodVisitor: MethodVisitorFactory,
    val targetClassFullyQualifiedName: String,
    val targetMethodBytecodeName: String,
    val targetMethodBytecodeDescriptor: String
) {
    fun isMatch(className: String, extension: GlancetPluginExtension): Boolean =
        isTargetClass(className) && featureSwitch(extension).get()

    fun isTargetClass(className: String): Boolean =
        targetClassFullyQualifiedName == className

    fun visitor(
        api: Int,
        methodVisitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ): MethodVisitor =
        if (targetMethodBytecodeName == name &&
            targetMethodBytecodeDescriptor == descriptor
        ) {
            createMethodVisitor(api, methodVisitor, access, name, descriptor)
        } else {
            methodVisitor
        }
}

private val RemoteAdapterModification =
    MethodModification(
        featureName =
            "remoteAdapter",
        featureSwitch =
            GlancetPluginExtension::remoteAdapter,
        createMethodVisitor =
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

internal class RemoteAdapterAdvice(
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
            INVOKEINTERFACE,
            /* owner = */
            "androidx/glance/Emittable",
            /* name = */
            "getModifier",
            /* descriptor = */
            "()Landroidx/glance/GlanceModifier;",
            /* isInterface = */
            true
        )

        visitMethodInsn(
            /* opcodeAndSource = */
            INVOKESTATIC,
            /* owner = */
            "dev/gonodono/glancet/remoteadapter/RemoteAdapterModifierKt",
            /* name = */
            "setRemoteAdapterIfPresent",
            /* descriptor = */
            "(Landroid/widget/RemoteViews;" +
                    "Landroidx/glance/GlanceModifier;" +
                    ")V",
            /* isInterface = */
            false
        )
    }
}

private val LazyColumnCompatModification =
    MethodModification(
        featureName =
            "lazyColumnCompat",
        featureSwitch =
            GlancetPluginExtension::lazyColumnCompat,
        createMethodVisitor =
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
    MethodModification(
        featureName =
            "lazyVerticalGridCompat",
        featureSwitch =
            GlancetPluginExtension::lazyVerticalGridCompat,
        createMethodVisitor =
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

internal class LazyCompatAdvice(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?
) : AfterAdvice(api, methodVisitor, access, name, descriptor) {

    override fun afterMethod() {
        // Landroid/widget/RemoteViews;
        visitVarInsn(ALOAD, 0)

        // Landroidx/glance/appwidget/lazy/EmittableLazy[Column|VerticalGrid];
        visitVarInsn(ALOAD, 2)
        visitTypeInsn(CHECKCAST, "androidx/glance/Emittable")
        visitMethodInsn(
            /* opcodeAndSource = */
            INVOKEINTERFACE,
            /* owner = */
            "androidx/glance/Emittable",
            /* name = */
            "getModifier",
            /* descriptor = */
            "()Landroidx/glance/GlanceModifier;",
            /* isInterface = */
            true
        )

        // Landroidx/glance/appwidget/InsertedViewInfo;
        visitVarInsn(ALOAD, 3)
        visitMethodInsn(
            /* opcodeAndSource = */
            INVOKEVIRTUAL,
            /* owner = */
            "androidx/glance/appwidget/InsertedViewInfo",
            /* name = */
            "getMainViewId",
            /* descriptor = */
            "()I",
            /* isInterface = */
            false
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
                    "Landroidx/glance/GlanceModifier;" +
                    "I" +
                    ")V",
            /* isInterface = */
            false
        )
    }
}

internal val Modifications: Set<MethodModification> =
    setOf(
        RemoteAdapterModification,
        LazyColumnCompatModification,
        LazyVerticalGridCompatModification
    )