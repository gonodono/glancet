package dev.gonodono.glancet.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

public class GlancetIssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val minApi: Int = api

    override val issues: List<Issue> =
        listOf(
            ActivationDetector.FeatureNotActivated,
            UsageDetector.RemoteAdapterInvalidComposable
        )

    override val vendor: Vendor =
        Vendor(
            vendorName = "Mike M.",
            identifier = "dev.gonodono.glancet",
            feedbackUrl = "https://github.com/gonodono/glancet/issues"
        )
}