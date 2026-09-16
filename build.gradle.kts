import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.*

plugins {
    kotlin("jvm") version "2.1.0" apply false
    kotlin("plugin.serialization") version "2.1.0" apply false
    id("org.jetbrains.kotlinx.kover").version("0.8.3")
    id("io.gitlab.arturbosch.detekt").version("1.23.3")
    id("org.jlleitschuh.gradle.ktlint").version("12.1.2")
}

group = "com.github.ppartisan.roky"

// Global static analysis configuration setup
ktlint {
    android = false
    reporters {
        reporter(CHECKSTYLE)
        reporter(JSON)
        reporter(HTML)
    }
    filter {
        exclude("**/style-violations.kt")
    }
}

// RESOLUTION: Safely hooks static analysis checks into the execution path
// without assuming a standard JVM 'build' task exists at the monolithic root container.
tasks.matching { it.name == "build" }.configureEach {
    dependsOn("ktlintCheck")
}

// Alternatively, you can run linting explicitly as a root-level gate check
tasks.register("checkQuality") {
    group = "verification"
    description = "Executes global static analysis rules across the codebase workspace."
    dependsOn("ktlintCheck", "detekt")
}

// =============================================================================
// GLOBAL SUBPROJECT TOOLCHAIN CONFIGURATION
// =============================================================================
subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(17)
        }
    }
}
