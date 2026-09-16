plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Reference our pure inner business module to satisfy contract ports
    implementation(project(":core"))

    // Central Dependency Graph Container (Koin)
    implementation("io.insert-koin:koin-core:3.5.3")

    // Concrete Infrastructure Third-Party Server Drivers (Supabase Ecosystem)
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.1.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.1.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.1.0")

    // Asynchronous Asynchronous Network Transmission Driver Engine (Ktor)
    implementation("io.ktor:ktor-client-cio:2.3.7")
}
