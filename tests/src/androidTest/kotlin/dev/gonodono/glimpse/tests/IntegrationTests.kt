package dev.gonodono.glimpse.tests

import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Intent
import android.view.View
import android.widget.ListView
import android.widget.StackView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.Matcher
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("Run manually")
// Tests don't work on Lollipop yet.
@SdkSuppress(minSdkVersion = 23)
@LargeTest
@RunWith(AndroidJUnit4::class)
class IntegrationTests {

    // RemoteAdapter

    @Test
    fun remoteAdapterListViewAdapter() =
        testAppWidget(ListViewWidget.Receiver::class.java) { device ->
            device.checkAdapterViewHasAdapter(R.id.list_view)
        }

    @Test
    fun remoteAdapterStackViewAdapter() =
        testAppWidget(StackViewWidget.Receiver::class.java) { device ->
            device.checkAdapterViewHasAdapter(R.id.stack_view)
        }

    @SdkSuppress(minSdkVersion = 31)
    @Test
    fun remoteAdapterListViewToPosition() =
        testAppWidget(ListViewWidget.Receiver::class.java) { device ->
            device.clickButtonAndCheckListViewIsScrolled(ToPosition)
        }

    @SdkSuppress(minSdkVersion = 31)
    @Test
    fun remoteAdapterListViewByOffset() =
        testAppWidget(ListViewWidget.Receiver::class.java) { device ->
            device.clickButtonAndCheckListViewIsScrolled(ByOffset)
        }

    @SdkSuppress(minSdkVersion = 31)
    @Test
    fun remoteAdapterStackViewDisplayedChild() =
        testAppWidget(StackViewWidget.Receiver::class.java) { device ->
            device.clickButtonAndCheckStackViewIsScrolled(DisplayedChild)
        }

    @SdkSuppress(minSdkVersion = 31)
    @Test
    fun remoteAdapterStackViewShowNext() =
        testAppWidget(StackViewWidget.Receiver::class.java) { device ->
            device.clickButtonAndCheckStackViewIsScrolled(ShowNext)
        }

    // ScrollableLazy

    @SdkSuppress(minSdkVersion = 31)
    @Test
    fun scrollableLazyListViewToPosition() =
        testAppWidget(ScrollableLazyWidget.Receiver::class.java) { device ->
            device.clickButtonAndCheckListViewIsScrolled(ToPosition)
        }

    @SdkSuppress(minSdkVersion = 31)
    @Test
    fun scrollableLazyListViewByOffset() =
        testAppWidget(ScrollableLazyWidget.Receiver::class.java) { device ->
            device.clickButtonAndCheckListViewIsScrolled(ByOffset)
        }
}

private fun <T : AppWidgetProvider> testAppWidget(
    provider: Class<T>,
    testBlock: (UiDevice) -> Unit
) {
    val device =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .apply { pressAndWaitForHome() }

    val context = ApplicationContext
    val intent =
        Intent(context, AppWidgetHostActivity::class.java)
            .putExtra(ExtraProvider, ComponentName(context, provider))

    ActivityScenario.launch<AppWidgetHostActivity>(intent).use { scenario ->
        device.waitForWidgetPrompt()
        testBlock(device)
    }
}

private fun UiDevice.waitForWidgetPrompt(timeout: Long = 2000L) {
    val promptSelector = By.text(listOf("allow", "access", "widget").pattern())
    val hasWidgetPrompt = this.wait(Until.hasObject(promptSelector), timeout)
    if (!hasWidgetPrompt) return

    val alwaysWords = listOf("always", "allow", "access")
    val always = this.findObject(By.text(alwaysWords.pattern()))
    always?.run { if (isCheckable && !isChecked) click() }

    val create = By.clazz(".*Button".pattern(true)).text("create".pattern())
    this.findObject(create)?.click()
}

private fun UiDevice.checkAdapterViewHasAdapter(adapterViewId: Int) {
    val resources = ApplicationContext.resources
    val resourceName = resources.getResourceName(adapterViewId)
    val hasAdapterView = this.wait(Until.hasObject(By.res(resourceName)), 2000L)
    check(hasAdapterView) { "Cannot find AdapterView" }

    onView(withId(adapterViewId)).check(matches(AdapterViewHasAdapterMatcher))
}

private fun UiDevice.clickButtonAndCheckListViewIsScrolled(text: String) =
    this.clickButtonAndCheck(
        buttonText = text,
        checkClazz = ListView::class.java,
        checkMatcher = AbsListViewIsScrolledMatcher
    )

private fun UiDevice.clickButtonAndCheckStackViewIsScrolled(text: String) =
    this.clickButtonAndCheck(
        buttonText = text,
        checkClazz = StackView::class.java,
        checkMatcher = AdapterViewAnimatorIsScrolledMatcher
    )

private fun <V : View> UiDevice.clickButtonAndCheck(
    buttonText: String,
    checkClazz: Class<V>,
    checkMatcher: Matcher<View>
) {
    val regex = buttonText.pattern()
    val button = this.wait(Until.findObject(By.text(regex)), 2000L)
    checkNotNull(button) { "Cannot find button: $buttonText" }
    button.click()

    Thread.sleep(1000L)  // Allows for potentially slow PendingIntents.

    onView(isAssignableFrom(checkClazz)).check(matches(checkMatcher))
}