package calebxzhou.craftcone.server

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlIndentation
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Created  on 2023-08-24,9:09.
 */
@Serializable
data class ConeServerConfig(
    val port:Int,
    val db :Db,
    val info:Info
){
    companion object{
        private const val CONF_FILE_NAME = "conf.toml"
        private val DEFAULT_CONF = ConeServerConfig(19198,
            Db("mongodb://127.0.0.1:27017/","craftcone"),
            Info(500,"CraftCone Server","Default Description","")
        )
        private val toml = Toml(
            inputConfig = TomlInputConfig(
                ignoreUnknownNames = true,
                allowEmptyValues = false,
                allowNullValues = false,
                allowEscapedQuotesInLiteralStrings = true,
                allowEmptyToml = false,
            ),
            outputConfig = TomlOutputConfig(
                indentation = TomlIndentation.TWO_SPACES
            )
        )
        fun loadConfig() = Path(CONF_FILE_NAME).run {
            if (!exists()) {
                logger.info { "Can't find config file, default config will be used" }
                DEFAULT_CONF.let {
                    saveConfig(it)
                    return@run it
                }
            }else{
                return@run try { toml.decodeFromString<ConeServerConfig>(Files.readString(this))
                }catch (e : Exception){
                    logger.error{ "Can't resolve config file" }
                    throw e
                }
            }

        }

        fun saveConfig(config: ConeServerConfig) = toml.encodeToString(config).run {
            Files.writeString(Path(CONF_FILE_NAME), this)
        }

    }

    @Serializable
    data class Db(
        val url:String,
        val dbName:String,
    )
    @Serializable
    data class Info(
        val maxPlayerLimit:Int,
        val serverName:String,
        val serverDescription:String,
        val serverIconBase64:String,
    )
}
