plugins {
    kotlin("jvm")
}

// NOTE: Local repositories block has been removed.
// Dependency resolution is now centrally managed by the root settings.gradle.kts file.

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("io.insert-koin:koin-core:3.5.0")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
