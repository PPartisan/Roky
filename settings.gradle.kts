pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// =============================================================================
// CENTRALIZED REPOSITORY MANAGEMENT REGISTRY
// =============================================================================
// Forces all isolated subprojects to pull dependencies from a single secure index.
// Prevents configuration drift and blocks unapproved external project repositories.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

// Registers our custom build-logic convention plugin architecture
includeBuild("build-logic")

// Root project identification name
rootProject.name = "Roky"

/**
 * Robust Multi-Module Project Registry
 */
include(":core") // Domain logic, core interfaces, and pure models
include(":infrastructure-supabase") // Third-party network drivers and database adapters
include(":app-tui") // Composition root and terminal user interface loop
