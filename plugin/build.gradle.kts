import com.vanniktech.maven.publish.GradlePublishPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.dokka)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
    explicitApi()
}

tasks.test {
    val tests = project.layout.projectDirectory.dir("..\\tests").asFile
    systemProperty("tests.directory", tests.absolutePath)
}

val rootProperties = Properties()
rootProperties.load(file("../gradle.properties").reader())

project.group = rootProperties.requireProperty("group.id")
project.version = rootProperties.requireProperty("plugin.version")

gradlePlugin {
    website = rootProperties.requireProperty("repository.url")
    vcsUrl = rootProperties.requireProperty("repository.git")

    plugins {
        register("glancet") {
            id = project.group.toString()
            implementationClass = "dev.gonodono.glancet.plugin.GlancetPlugin"
            displayName = "Glancet Gradle plugin"
            description = "A Gradle plugin required by the Glancet library."
            tags = listOf("android", "android-library")
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    configure(GradlePublishPlugin())

    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString()
    )

    pom {
        name =
            "Glancet Gradle plugin"
        description =
            "A Gradle plugin required by the Glancet library."
        url =
            "https://github.com/gonodono/glancet/tree/main/plugin"
        inceptionYear =
            "2025"

        scm {
            url =
                "https://github.com/gonodono/glancet"
            connection =
                "scm:git:git://github.com/gonodono/glancet.git"
            developerConnection =
                "scm:git:ssh://github.com/gonodono/glancet.git"
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
    modulePath = rootProject.name + project.path.replace(":", "/")
    dokkaSourceSets.configureEach { includes.from("docs.module.md") }

    dokkaPublications {
        html {
            outputDirectory = rootProject.layout.buildDirectory.dir("docs")
        }
    }
}

dependencies {

    compileOnly(libs.asm.commons)
    compileOnly(libs.asm.util)
    compileOnly(libs.android.gradle.plugin)

    testImplementation(libs.junit)
    testImplementation(gradleTestKit())
    testImplementation(libs.asm.commons)
    testImplementation(libs.asm.util)
    testImplementation(libs.android.gradle.plugin)

    dokkaHtmlPlugin(libs.dokka.versioning.plugin)
}

fun Properties.requireProperty(name: String): String =
    requireNotNull(this[name]) { "Cannot find property: $name" }.toString()