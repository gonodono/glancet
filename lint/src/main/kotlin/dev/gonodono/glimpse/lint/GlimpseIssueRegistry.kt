package dev.gonodono.glimpse.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

public class GlimpseIssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val minApi: Int = api

    override val issues: List<Issue> =
        listOf(
            ActivationDetector.GlimpseFeatureNotActivated,
            UsageDetector.RemoteAdapterInvalidComposable
        )

    override val vendor: Vendor =
        Vendor(
            vendorName = "Mike M.",
            identifier = "dev.gonodono.glimpse",
            feedbackUrl = "https://github.com/gonodono/glimpse/issues"
        )
}