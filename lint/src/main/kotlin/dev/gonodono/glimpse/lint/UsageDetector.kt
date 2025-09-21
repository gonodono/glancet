package dev.gonodono.glimpse.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import dev.gonodono.glimpse.lint.util.AndroidRemoteViews
import dev.gonodono.glimpse.lint.util.RemoteAdapter
import dev.gonodono.glimpse.lint.util.getContainingComposableEmitter
import dev.gonodono.glimpse.lint.util.isSameClassAs
import dev.gonodono.glimpse.lint.util.report
import org.jetbrains.uast.UCallExpression
import java.util.EnumSet

public class UsageDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String>? =
        listOf(RemoteAdapter.name)

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
        if (!method.isSameClassAs(RemoteAdapter)) return

        val composable = node.getContainingComposableEmitter() ?: return
        if (composable == AndroidRemoteViews) return

        context.report(RemoteAdapterInvalidComposable, node, method)
    }

    internal companion object {

        val RemoteAdapterInvalidComposable: Issue =
            Issue.create(
                id =
                    "RemoteAdapterInvalidComposable",
                briefDescription =
                    "Invalid Composable for remoteAdapter",
                explanation =
                    "remoteAdapter works only with AndroidRemoteViews.",
                category =
                    Category.CORRECTNESS,
                priority =
                    7,
                severity =
                    Severity.ERROR,
                implementation =
                    Implementation(
                        UsageDetector::class.java,
                        EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES)
                    )
            )
    }
}