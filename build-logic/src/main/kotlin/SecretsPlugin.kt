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

                val useLocal = GRADLE_PROPS_FILE.asRootFile().asProps().filter { it.key == KEY_USE_LOCAL_MOCKS }
                if(useLocal.isTrue())
                    println(USE_LOCAL_MOCKS_MSG)
                else if(useLocal.isFalse() && !secrets.exists())
                    error(MISSING_SECRET_PROPS_ERROR_MSG)

                dir.get().asFile.resolve("$OUTPUT_FILE.kt").apply {
                    parentFile.mkdirs()
                    val text = listOf(useLocal, secrets.asProps())
                        .flatMap { it.entries }
                        .fold(setOf<Pair<String, String>>()) { acc, it -> acc + setOf(it.toStringPair()) }
                        .asConstants()
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
        private const val GRADLE_PROPS_FILE = "gradle.properties"
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

            Alternatively, to use source code mocks, set 'USE_LOCAL_MOCKS=true' under 'gradle.properties'.
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        """.trimIndent()

        private fun File.asProps() : Properties = Properties().apply {
            if(exists())
                inputStream().use { load(it) }
        }

        private fun Map<*,*>.isTrue() =
            this[KEY_USE_LOCAL_MOCKS]?.toString()?.toBooleanStrictOrNull()?:false
        private fun Map<*,*>.isFalse() =
            !isTrue()

        private fun Set<Pair<String,String>>.asConstants() : String = buildString {
            header()
            this@asConstants.forEach { appendLine(it.toConstant()) }
            footer()
        }

        private fun Map.Entry<*,*>.toStringPair() =
            "$key" to "$value"

        private fun Pair<String,String>.toConstant() : String =
            (if(second.isBoolean()) second else "\"$second\"").let {
                """    const val $first = $it"""
            }

        private fun String.isBoolean() : Boolean =
            toBooleanStrictOrNull() != null

        private fun StringBuilder.header() =
            appendLine("object $OUTPUT_FILE {")
        private fun StringBuilder.footer() =
            appendLine("}")
    }
}
