package dev.gonodono.glancet.lint.util

import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.TextFormat
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

internal const val LintTokensDirectory = "outputs/glancet/"

internal val JavaContext.isRunningInUnitTest: Boolean
    get() = this.client.getClientDisplayName() == "Lint Unit Tests"

internal fun String.uncapitalized(): String =
    when (this.length) {
        0 -> this
        1 -> this[0].lowercase()
        else -> "${this[0].lowercase()}${this.substring(1)}"
    }

internal fun PsiMethod.isSameClassAs(function: Function): Boolean =
    this.containingClass?.qualifiedName == function.fullyQualifiedClassName

internal fun JavaContext.report(
    issue: Issue,
    node: UCallExpression,
    method: PsiMethod
) {
    if (this.isSuppressedWithComment(method, issue)) return

    val location = this.getNameLocation(node)
    val message = issue.getExplanation(TextFormat.TEXT)
    this.report(issue, location, message)
}