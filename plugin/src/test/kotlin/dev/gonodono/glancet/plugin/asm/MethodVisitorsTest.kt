package dev.gonodono.glancet.plugin.asm

import android.widget.RemoteViews
import androidx.glance.Emittable
import androidx.glance.appwidget.InsertedViewInfo
import androidx.glance.appwidget.TranslationContext
import dev.gonodono.glancet.plugin.asm.fake.lazyCompatTarget
import dev.gonodono.glancet.plugin.asm.fake.remoteAdapterTarget
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
    fun lazyCompatAdvice() {
        val remoteViews = RemoteViews()
        val context = TranslationContext()
        val emittable = object : Emittable {}
        val info = InsertedViewInfo()
        remoteViews.assertUnused()

        lazyCompatTarget(remoteViews, context, emittable, info)
        remoteViews.assertUnused()

        val reader = ClassReader(LazyCompatTargetKt)
        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        val visitor =
            TestClassVisitor(
                classVisitor = writer,
                methodName = LazyCompatTargetMethod,
                createMethodVisitor = ::LazyCompatAdvice
            )
        reader.accept(visitor, 0)

        val bytes = writer.toByteArray()
        val modified =
            TestClassLoader().define(LazyCompatTargetKt, bytes, 0, bytes.size)!!
        val method =
            modified.getDeclaredMethod(
                LazyCompatTargetMethod,
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
    "dev.gonodono.glancet.plugin.asm.fake.RemoteAdapterTargetKt"

internal const val LazyCompatTargetMethod = "lazyCompatTarget"

internal const val LazyCompatTargetKt =
    "dev.gonodono.glancet.plugin.asm.fake.LazyCompatTargetKt"

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