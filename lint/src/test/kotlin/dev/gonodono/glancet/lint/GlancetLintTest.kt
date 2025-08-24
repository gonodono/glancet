package dev.gonodono.glancet.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import org.intellij.lang.annotations.Language

abstract class GlancetLintTest : LintDetectorTest() {

    protected fun runTest(
        @Language("kotlin")
        testFileContent: String,
        vararg otherFiles: TestFile,
        expectedText: String = "No warnings."
    ) {
        lint()
            .allowMissingSdk()
            .files(
                ComposableStub,
                GlanceModifierStub,
                AndroidRemoteViewsStub,
                RemoteAdapterStub,
                LazyColumnCompatStub,
                LazyVerticalGridCompatStub,
                DummyComposableStub,
                kotlin(
                    """
                    import androidx.glance.GlanceModifier
                    import androidx.glance.appwidget.AndroidRemoteViews
                    import dev.gonodono.glancet.lazycompat.LazyColumnCompat
                    import dev.gonodono.glancet.lazycompat.LazyVerticalGridCompat
                    import dev.gonodono.glancet.remoteadapter.remoteAdapter
                    import test.pkg.DummyComposable
                    import test.pkg.DummyComposable2
                    import test.pkg.extension1
                    import test.pkg.extension2
                    
                    $testFileContent
                    """
                        .trimIndent()
                ),
                *otherFiles
            )
            .run()
            .expect(expectedText)
    }
}