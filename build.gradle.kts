
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.*

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlinx.kover").version("0.8.3")
    id("io.gitlab.arturbosch.detekt").version("1.23.3")
    id("org.jlleitschuh.gradle.ktlint").version("12.1.2")
}

group = "com.github.ppartisan.roky"
version = "1.0-SNAPSHOT"

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

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "3.0.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    implementation("org.slf4j:slf4j-simple:2.0.16")

    implementation("com.googlecode.lanterna:lanterna:3.1.1")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")

    implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.6"))
    implementation("io.insert-koin:koin-core")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn("ktlintCheck")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

kotlin {
    jvmToolchain(17)
}
