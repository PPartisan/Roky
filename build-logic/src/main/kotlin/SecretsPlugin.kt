import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.util.Properties

abstract class SecretsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        fun String.asRootFile() =
            project.rootProject.file(this)

        val secrets = SECRETS_FILE.asRootFile()
        val dir = project.layout.buildDirectory.dir(OUTPUT_DIR)

        val generateSecrets = project.tasks.register(TASK_NAME) {
            outputs.dir(dir)
            doLast {
                if (!secrets.exists())
                    error(MISSING_SECRET_PROPS_ERROR_MSG)

                val props = secrets.asProps()
                if(props.useLocalMocks())
                    println(USE_LOCAL_MOCKS_MSG)

                dir.get().asFile.resolve("$OUTPUT_FILE.kt").apply {
                    parentFile.mkdirs()
                    val text = with(props) {
                        if(useLocalMocks()) asUseLocalMocksText() else asSecretsText()
                    }
                    writeText(text)
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
        private const val SECRETS_FILE = "secrets.properties"
        private const val KEY_USE_LOCAL_MOCKS = "USE_LOCAL_MOCKS"
        private const val OUTPUT_DIR = "generated/secrets"
        private const val OUTPUT_FILE = "Secrets"
        private const val TASK_NAME = "generateSecrets"

        private val USE_LOCAL_MOCKS_MSG = """
           ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
           ℹ️ INFO
           ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            This project's '${SECRETS_FILE}' either does not contain the key '$KEY_USE_LOCAL_MOCKS' or its value is
            set to true.

            To use a local instance of Supabase-CLI, set this property to false and follow the instructions on the Roky
            Wiki page under 'Project Setup' → 'Setup Supabase CLI':
              > https://github.com/PPartisan/Roky/wiki/Project-Setup#3-setup-supabase-cli
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent()

        private val MISSING_SECRET_PROPS_ERROR_MSG = """
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            ⚠️ WARNING
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Your project does not contain a '$SECRETS_FILE' file. Roky requires this file to run locally as it contains
            information about your local Supabase-CLI setup.

            To build Roky, create a file named '$SECRETS_FILE' in the root directory of this project and add the values
            specified in the Roky Wiki page under 'Project Setup' → 'Setup Supabase CLI':
              > https://github.com/PPartisan/Roky/wiki/Project-Setup#3-setup-supabase-cli
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent()

        private fun File.asProps() : Properties = Properties().apply {
            inputStream().use { load(it) }
        }

        private fun Properties.getBool(key: String, default: Boolean = true) =
            getProperty(key)?.toBoolean()?:default
        private fun Properties.useLocalMocks() =
            getBool(KEY_USE_LOCAL_MOCKS)

        private fun Properties.asSecretsText() : String = buildString {
            header()
            writeTo(KEY_USE_LOCAL_MOCKS, getBool(KEY_USE_LOCAL_MOCKS))
            this@asSecretsText.filterNot { it.key == KEY_USE_LOCAL_MOCKS }.forEach {
                when(val value = it.value) {
                    is Boolean -> writeTo(it.key, value)
                    is String -> writeTo(it.key, value)
                }
            }
            footer()
        }

        private fun Properties.asUseLocalMocksText() : String = buildString {
            header()
            writeTo(KEY_USE_LOCAL_MOCKS, getBool(KEY_USE_LOCAL_MOCKS))
            footer()
        }
        private fun StringBuilder.writeTo(key: Any , value: String) =
           appendLine("""    const val $key = "$value"""")
        private fun StringBuilder.writeTo(key: Any , value: Boolean) =
            appendLine("""    const val $key = $value""")



        private fun StringBuilder.header() =
            appendLine("object $OUTPUT_FILE {")
        private fun StringBuilder.footer() =
            appendLine("}")
    }
}
