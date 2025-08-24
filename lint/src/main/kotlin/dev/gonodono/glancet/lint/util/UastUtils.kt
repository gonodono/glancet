package dev.gonodono.glancet.lint.util

import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.getContainingDeclaration
import org.jetbrains.uast.getOutermostQualified
import org.jetbrains.uast.tryResolve

internal fun UCallExpression.getContainingComposableEmitter(): Function? {
    val outerExpression = this.getOutermostQualified() ?: return null

    val composable =
        this.containingElementsWithinDeclaration
            .map { it as? UCallExpression }
            .filterNotNull()
            .firstOrNull { call ->
                call.isComposableEmitter &&
                        call.valueArguments.contains(outerExpression)
            }

    val name = composable?.methodName ?: return null
    val fqcn = composable.fullyQualifiedClassName ?: return null

    return Function(name, fqcn)
}

internal val UElement.containingElementsWithinDeclaration: Sequence<UElement>
    get() {
        val declaration = this.getContainingDeclaration()
        return this.containingElements.takeWhile { it != declaration }
    }

internal val UElement.containingElements: Sequence<UElement>
    get() = generateSequence(this.uastParent, UElement::uastParent)

internal val UCallExpression.isComposableEmitter: Boolean
    get() = this.returnType?.canonicalText == UnitFqcn && this.isComposable

internal val UCallExpression.isComposable: Boolean
    get() {
        val method = this.tryResolve() as? PsiMethod ?: return false
        return method.hasAnnotation(ComposableFqcn)
    }

internal val UCallExpression.fullyQualifiedClassName: String?
    get() = (this.tryResolve() as? PsiMethod)?.containingClass?.qualifiedName