import java.io.File
import java.io.IOException
import java.util.Properties

object Secrets {
    private const val SECRETS_FILE = "secrets.properties"
    private val props: Properties =
        Properties().apply {
            val file = File(System.getProperty("user.dir"), SECRETS_FILE)
            if (!file.exists()) {
                throw IOException("${file.name} does not exist.")
            }
            file.inputStream().use {
                load(it)
            }
        }

    private fun String.property(): String =
        props.getProperty(this) ?: throw IOException("Missing mandatory property: $this")

    val clientKey: String
        get() = "CLIENT_API_KEY".property()
    val serverUrl: String
        get() = "SERVER_URL".property()
}
