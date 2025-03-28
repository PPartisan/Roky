plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("secretsPlugin") {
            id = "secrets-plugin"
            implementationClass = "SecretsPlugin"
        }
    }
}
