package dev.gonodono.glancet.tests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE

internal inline val ApplicationContext: Context
    get() = ApplicationProvider.getApplicationContext()

internal fun UiDevice.pressAndWaitForHome(timeout: Long = 2000L) {
    this.pressHome()

    val launcherPackage = this.launcherPackageName ?: return
    this.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), timeout)
}

internal fun String.pattern(caseSensitive: Boolean = false): Pattern =
    Pattern.compile(this, if (caseSensitive) 0 else CASE_INSENSITIVE)

internal fun List<String>.pattern(caseSensitive: Boolean = false): Pattern {
    val regex = buildString {
        this@pattern.forEach { append("(?=.*$it)") }
        append(".*")
    }
    return Pattern.compile(regex, if (caseSensitive) 0 else CASE_INSENSITIVE)
}