import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
    explicitApi()
}

val rootProperties = Properties()
rootProperties.load(file("../gradle.properties").reader())

group = rootProperties.requireProperty("group.id")
version = rootProperties.requireProperty("library.version")

gradlePlugin {
    website = rootProperties.requireProperty("repository.url")
    vcsUrl = rootProperties.requireProperty("repository.git")

    plugins {
        register("glancet") {
            id = "dev.gonodono.glancet"
            displayName = "Gradle Plugin for Glancet"
            description = "A plugin for the Glancet library, tools and " +
                    "extended composables for Glance app widgets on Android."
            tags = listOf("android", "android-library")
            implementationClass = "dev.gonodono.glancet.plugin.GlancetPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.asm.commons)
    compileOnly(libs.asm.util)
}

fun Properties.requireProperty(name: String): String =
    requireNotNull(this.getProperty(name)) { "Cannot find property: $name" }