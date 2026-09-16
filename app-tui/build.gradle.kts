plugins {
    kotlin("jvm")
    application            // Declares this module outputs the executable app binary target
    id("secrets-plugin")   // Applies your custom SecretsPlugin to auto-generate constants
}

application {
    // RESOLUTION: Configures the precise compiled JVM class target name for package-less files
    mainClass.set("MainKt")
}

dependencies {
    // App-TUI functions as the Application Composition Root by wiring modules together
    implementation(project(":core"))
    implementation(project(":infrastructure-supabase"))

    // Enforce explicit asynchronous driver engine boundaries inside the UI layer
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Lightweight terminal UI rendering framework
    implementation("com.googlecode.lanterna:lanterna:3.1.1")

    // Central dependency injection framework for managing window lifecycles
    implementation("io.insert-koin:koin-core:3.5.0")

    // RESOLUTION: Explicitly inherit transit Markdown rendering engines and network dependencies
    implementation("com.vladsch.flexmark:flexmark:0.64.8")
    implementation("io.ktor:ktor-client-core:2.3.7")

    testImplementation(kotlin("test"))
}
