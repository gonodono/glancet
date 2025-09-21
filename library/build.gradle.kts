import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Year

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.dokka)
}

android {
    namespace = "dev.gonodono.glimpse"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
    explicitApi()
}

project.group = requireProperty("group.id")
project.version = requireProperty("library.version")

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = rootProject.name,
        version = project.version.toString()
    )

    pom {
        name =
            "Glimpse"
        description =
            "Tools and extended composables for Glance app widgets on Android."
        url =
            "https://github.com/gonodono/glimpse"
        inceptionYear =
            "2025"

        scm {
            url =
                "https://github.com/gonodono/glimpse"
            connection =
                "scm:git:git://github.com/gonodono/glimpse.git"
            developerConnection =
                "scm:git:ssh://github.com/gonodono/glimpse.git"
        }
        licenses {
            name = "The MIT License"
            url = "https://opensource.org/license/mit"
        }
        developers {
            developer {
                id = "gonodono"
                name = "Mike M."
                email = "gonodono137@gmail.com"
            }
        }
    }
}

dokka {
    moduleName = rootProject.name
    modulePath = rootProject.name + project.path.replace(":", "/")

    dokkaSourceSets.configureEach {
        includes.from("docs.module.md")

        pluginsConfiguration {
            html {
                homepageLink = requireProperty("repository.url")
                footerMessage =
                    "© ${Year.now().value} ${requireProperty("developer.name")}"
            }
        }

        sourceLink {
            localDirectory = project.layout.projectDirectory.dir("src")
            val repoUrl = requireProperty("repository.url")
            remoteUrl = uri("$repoUrl/tree/main/${project.name}/src")
            remoteLineSuffix = "#L"
        }
    }
}

dependencies {

    implementation(libs.androidx.glance)
    implementation(libs.androidx.remoteviews)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.glance.testing)
    testImplementation(libs.androidx.glance.appwidget.testing)
    testImplementation(libs.roboelectric)

    lintPublish(projects.lint)
    dokkaHtmlPlugin(libs.dokka.versioning.plugin)
}

fun Project.requireProperty(name: String): String =
    requireNotNull(this.properties[name]) { "Cannot find property: $name" }
        .toString()