package dev.gonodono.glancet.tests

import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterViewAnimator
import androidx.test.espresso.matcher.BoundedDiagnosingMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

internal val AdapterViewHasAdapterMatcher: Matcher<View> =
    BoundedViewMatcher<AdapterView<*>>(
        AdapterView::class.java,
        "AdapterView.getAdapter() is not null",
        { it.adapter != null },
        "AdapterView.getAdapter() is null"
    )

internal val AbsListViewIsScrolledMatcher: Matcher<View> =
    BoundedViewMatcher<AbsListView>(
        AbsListView::class.java,
        "ListView is scrolled",
        { it.firstVisiblePosition > 0 },
        "ListView is not scrolled"
    )

internal val AdapterViewAnimatorIsScrolledMatcher: Matcher<View> =
    BoundedViewMatcher<AdapterViewAnimator>(
        AdapterViewAnimator::class.java,
        "StackView is scrolled",
        { it.displayedChild > 0 },
        "StackView is not scrolled"
    )

internal class BoundedViewMatcher<V : View>(
    expected: Class<out View>,
    private val descriptionText: String,
    private val matchCondition: (V) -> Boolean,
    private val mismatchDescriptionText: String
) : BoundedDiagnosingMatcher<View, V>(expected) {

    override fun describeMoreTo(description: Description) {
        description.appendText(descriptionText)
    }

    override fun matchesSafely(
        item: V,
        mismatchDescription: Description
    ): Boolean {
        if (matchCondition(item)) return true
        mismatchDescription.appendText(mismatchDescriptionText)
        return false
    }
}