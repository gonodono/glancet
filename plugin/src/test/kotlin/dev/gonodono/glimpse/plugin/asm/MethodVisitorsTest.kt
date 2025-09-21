package dev.gonodono.glimpse.plugin.asm

import android.widget.RemoteViews
import androidx.glance.Emittable
import androidx.glance.appwidget.InsertedViewInfo
import androidx.glance.appwidget.TranslationContext
import dev.gonodono.glimpse.plugin.asm.fake.scrollableLazyTarget
import dev.gonodono.glimpse.plugin.asm.fake.remoteAdapterTarget
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@RunWith(JUnit4::class)
class MethodVisitorsTest {

    @Test
    fun remoteAdapterAdvice() {
        val remoteViews = RemoteViews()
        val context = TranslationContext()
        val emittable = object : Emittable {}
        remoteViews.assertUnused()

        remoteAdapterTarget(remoteViews, context, emittable)
        remoteViews.assertUnused()

        val reader = ClassReader(RemoteAdapterTargetKt)
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        val visitor =
            TestClassVisitor(
                classVisitor = writer,
                methodName = RemoteAdapterTargetMethod,
                createMethodVisitor = ::RemoteAdapterAdvice
            )
        reader.accept(visitor, 0)

        val bytes = writer.toByteArray()
        val modified =
            TestClassLoader().define(
                name = RemoteAdapterTargetKt,
                b = bytes,
                off = 0,
                len = bytes.size
            )!!
        val method =
            modified.getDeclaredMethod(
                RemoteAdapterTargetMethod,
                RemoteViews::class.java,
                TranslationContext::class.java,
                Emittable::class.java
            )

        method.invoke(null, remoteViews, context, emittable)
        remoteViews.assertUsed()
    }

    @Test
    fun scrollableLazyAdvice() {
        val remoteViews = RemoteViews()
        val context = TranslationContext()
        val emittable = object : Emittable {}
        val info = InsertedViewInfo()
        remoteViews.assertUnused()

        scrollableLazyTarget(remoteViews, context, emittable, info)
        remoteViews.assertUnused()

        val reader = ClassReader(ScrollableLazyTargetKt)
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        val visitor =
            TestClassVisitor(
                classVisitor = writer,
                methodName = ScrollableLazyTargetMethod,
                createMethodVisitor = ::ScrollableLazyAdvice
            )
        reader.accept(visitor, 0)

        val bytes = writer.toByteArray()
        val modified =
            TestClassLoader().define(ScrollableLazyTargetKt, bytes, 0, bytes.size)!!
        val method =
            modified.getDeclaredMethod(
                ScrollableLazyTargetMethod,
                RemoteViews::class.java,
                TranslationContext::class.java,
                Emittable::class.java,
                InsertedViewInfo::class.java,
            )

        method.invoke(null, remoteViews, context, emittable, info)
        remoteViews.assertUsed()
    }
}

internal const val RemoteAdapterTargetMethod = "remoteAdapterTarget"

internal const val RemoteAdapterTargetKt =
    "dev.gonodono.glimpse.plugin.asm.fake.RemoteAdapterTargetKt"

internal const val ScrollableLazyTargetMethod = "scrollableLazyTarget"

internal const val ScrollableLazyTargetKt =
    "dev.gonodono.glimpse.plugin.asm.fake.ScrollableLazyTargetKt"

internal fun RemoteViews.assertUsed() =
    assertTrue("RemoteViews has not been used", this.isUsed)

internal fun RemoteViews.assertUnused() =
    assertFalse("RemoteViews used unexpectedly", this.isUsed)

private class TestClassVisitor(
    classVisitor: ClassVisitor,
    private val methodName: String,
    private val createMethodVisitor: MethodVisitorFactory
) : ClassVisitor(Opcodes.ASM9, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String?>?
    ): MethodVisitor? {
        val methodVisitor =
            super.visitMethod(access, name, descriptor, signature, exceptions)

        return if (name == methodName && methodVisitor != null) {
            createMethodVisitor(api, methodVisitor, access, name, descriptor)
        } else {
            methodVisitor
        }
    }
}

private class TestClassLoader : ClassLoader() {

    fun define(name: String, b: ByteArray, off: Int, len: Int): Class<*>? =
        defineClass(name, b, off, len)
}