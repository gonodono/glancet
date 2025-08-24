import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Year

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

android {
    namespace = "dev.gonodono.glancet"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
    explicitApi()
}

dokka {
    val rootDir = rootProject.layout.projectDirectory

    basePublicationsDirectory = rootDir
    moduleName = rootProject.name

    pluginsConfiguration {
        html {
            customAssets.from(rootDir.dir("images").file("logo-icon.svg"))
            homepageLink = requireProperty("repository.url")
            footerMessage =
                "© ${Year.now().value} ${requireProperty("developer.name")}"
        }

        versioning {
            // olderVersionsDir = TBD
            version = requireProperty("library.version")
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = requireProperty("group.id")
                artifactId = "glancet"
                version = requireProperty("library.version")
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.glance)
    implementation(libs.androidx.remoteviews)

    lintPublish(projects.lint)

    dokkaHtmlPlugin(libs.dokka.versioning.plugin)
}

fun Project.requireProperty(name: String): String =
    requireNotNull(this.properties[name]) { "Cannot find property: $name" }
        .toString()