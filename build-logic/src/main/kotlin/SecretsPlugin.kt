import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.util.Properties

abstract class SecretsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        fun String.asProps(filterBy: (Map.Entry<*, *>) -> Boolean = { true }): Properties =
            project.rootProject.file(this).asProps().filter(filterBy).asProperties()

        fun String.asDir() =
            project.layout.buildDirectory.dir(this)

        val secrets = SECRETS_PROPERTIES_FILE.asProps()
            .useEnvOverridesIfTheyExist(KEY_SERVER_URL, KEY_CLIENT_KEY)
        val local = GRADLE_PROPERTIES_FILE.asProps {
            it.key == KEY_USE_LOCAL_MOCKS
        }.useEnvOverridesIfTheyExist(KEY_USE_LOCAL_MOCKS)
        val dir = OUTPUT_DIR.asDir()

        val generateSecrets = project.tasks.register(TASK_NAME) {
            outputs.dir(dir)
            doLast {
                val useLocal = local.isTrue()
                val properties = when {
                    useLocal -> onUseLocalMocks()
                    !useLocal && !secrets.isValid() -> onUseRemoteButNotSecrets()
                    else -> listOf(local, secrets)
                }

                dir.createFile(OUTPUT_FILE) {
                    parentFile.mkdirs()
                    writeText(properties.asText())
                }
            }
        }

        project.afterEvaluate {
            project.extensions.configure<SourceSetContainer>("sourceSets") {
                getByName("main").java.srcDir(dir)
            }
            tasks.matching { it.name.matches(Regex(".*[k|K](?:otlin|tlint).*")) }.configureEach {
                dependsOn(generateSecrets)
            }
        }

    }

    companion object {
        private const val GRADLE_PROPERTIES_FILE = "gradle.properties"
        private const val SECRETS_PROPERTIES_FILE = "secrets.properties"
        private const val OUTPUT_DIR = "generated/secrets"
        private const val OUTPUT_FILE = "Secrets"
        private const val TASK_NAME = "generateSecrets"

        private const val KEY_USE_LOCAL_MOCKS = "USE_LOCAL_MOCKS"
        private const val KEY_SERVER_URL = "SERVER_URL"
        private const val KEY_CLIENT_KEY = "CLIENT_KEY"

        private val useLocalMocksMsg = """
           ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
           ℹ️ INFO
           ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            This project's '${GRADLE_PROPERTIES_FILE}' either does not contain the key '$KEY_USE_LOCAL_MOCKS' or its value is
            set to true.

            To use a local instance of Supabase-CLI, set this property to false and follow the instructions on the Roky
            Wiki page under 'Project Setup' → 'Setup Supabase CLI':
              > https://github.com/PPartisan/Roky/wiki/Project-Setup#3-setup-supabase-cli
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent()

        private val missingSecretPropsErrorMsg = """
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            ⚠️ WARNING
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Your project either does not contain a '$SECRETS_PROPERTIES_FILE' file, or that file is empty. Roky requires this file
            to run locally as it contains information about your local Supabase-CLI setup.

            To build Roky, create a file named '$SECRETS_PROPERTIES_FILE' in the root directory of this project and add the values
            specified in the Roky Wiki page under 'Project Setup' → 'Setup Supabase CLI':
              > https://github.com/PPartisan/Roky/wiki/Project-Setup#3-setup-supabase-cli

            Your '$SECRETS_PROPERTIES_FILE' must contain these variables at a minimum:
              ${listOf(KEY_CLIENT_KEY, KEY_SERVER_URL)}

            Alternatively, to use source code mocks, set '$KEY_USE_LOCAL_MOCKS=true' under '$GRADLE_PROPERTIES_FILE'.

            This task will generate minimal default values to compile.
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent()

        private fun Properties.useEnvOverridesIfTheyExist(vararg keys: String): Properties = apply {
            keys.forEach { key ->
                System.getenv(key)?.let { envValue -> this[key] = envValue }
            }
        }

        private fun Map<*, *>.asProperties(): Properties =
            Properties().apply { putAll(this@asProperties) }

        private fun Provider<Directory>.createFile(file: String, block: File.() -> Unit) =
            get().asFile.resolve("$file.kt").apply(block)

        private fun Properties.isValid(): Boolean {
            if (isEmpty)
                return false
            return containsKey(KEY_CLIENT_KEY) && containsKey(KEY_SERVER_URL)
        }

        private fun onUseLocalMocks(): List<Properties> {
            println(useLocalMocksMsg)
            return defaults()
        }

        private fun onUseRemoteButNotSecrets(): List<Properties> {
            println(missingSecretPropsErrorMsg)
            return defaults()
        }

        private fun File.asProps(): Properties = Properties().apply {
            if (exists())
                inputStream().use { load(it) }
        }

        private fun defaults(): List<Properties> = Properties().apply {
            this[KEY_USE_LOCAL_MOCKS] = true
            listOf(KEY_SERVER_URL, KEY_CLIENT_KEY).forEach { this[it] = "" }
        }.let(::listOf)

        private fun Map<*, *>.isTrue() =
            this[KEY_USE_LOCAL_MOCKS]?.toString()?.toBooleanStrictOrNull() ?: false

        private fun Collection<Properties>.asText(): String =
            flatMap { it.entries }
                .fold(setOf<Pair<String, String>>()) { acc, it -> acc + setOf(it.toStringPair()) }
                .asText()

        private fun Set<Pair<String, String>>.asText(): String = buildString {
            header()
            this@asText.forEach { appendLine(it.toConstant()) }
            footer()
        }

        private fun Map.Entry<*, *>.toStringPair() =
            "$key" to "$value"

        private fun Pair<String, String>.toConstant(): String =
            (if (second.isBoolean()) second else "\"$second\"").let {
                """    const val $first = $it"""
            }

        private fun String.isBoolean(): Boolean =
            toBooleanStrictOrNull() != null

        private fun StringBuilder.header() =
            appendLine("object $OUTPUT_FILE {")

        private fun StringBuilder.footer() =
            appendLine("}")
    }
}
