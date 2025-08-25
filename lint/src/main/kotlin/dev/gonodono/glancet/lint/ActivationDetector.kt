package dev.gonodono.glancet.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import dev.gonodono.glancet.lint.util.ActivationTargets
import dev.gonodono.glancet.lint.util.LintSignalDirectory
import dev.gonodono.glancet.lint.util.isRunningInUnitTest
import dev.gonodono.glancet.lint.util.isSameClassAs
import dev.gonodono.glancet.lint.util.report
import dev.gonodono.glancet.lint.util.uncapitalized
import org.jetbrains.uast.UCallExpression
import java.io.File
import java.util.EnumSet

public class ActivationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String>? =
        ActivationTargets.map { it.name }

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
        val target =
            ActivationTargets.firstOrNull { it.name == method.name }
                ?: return

        if (!method.isSameClassAs(target)) return

        val buildDirectory =
            if (!context.isRunningInUnitTest) {
                context.project.buildModule?.buildFolder ?: return
            } else {
                File(context.project.dir, "build")
            }
        val variantName =
            if (!context.isRunningInUnitTest) {
                context.project.buildVariant?.name ?: return
            } else {
                "test"
            }

        val directory = File(buildDirectory, LintSignalDirectory)
        val featureName = target.name.uncapitalized()
        if (File(directory, "$variantName.$featureName").exists()) return

        context.report(FeatureNotActivated, node, method)
    }

    internal companion object {

        val FeatureNotActivated: Issue =
            Issue.create(
                id =
                    "FeatureNotActivated",
                briefDescription =
                    "Missing Glancet plugin activation",
                explanation =
                    "Glancet features must be activated with the plugin. " +
                            "Be sure to rebuild after adding or updating it.",
                category =
                    Category.CORRECTNESS,
                priority =
                    7,
                severity =
                    Severity.ERROR,
                implementation =
                    Implementation(
                        ActivationDetector::class.java,
                        EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES)
                    )
            )
    }
}