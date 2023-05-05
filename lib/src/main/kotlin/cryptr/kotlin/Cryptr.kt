package cryptr.kotlin

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import cryptr.kotlin.enums.CryptrApiPath
import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.objects.Constants
import io.github.oshai.KotlinLogging
import kotlinx.serialization.json.Json
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

const val DEFAULT_BASE_URL = "https://auth.cryptr.eu"
const val DEFAULT_REDIRECT_URL = "http://localhost:8080/callback"

/**
 * Instantiate Cryptr
 *
 * @param tenantDomain Your account value `domain`
 * @param baseUrl The URL of your Cryptr Service
 * @param defaultRedirectUrl Where you want to redirect te end-user after dev.cryptr.eu process
 * @param apiKeyClientId The ID of your API KEY
 * @param apiKeyClientSecret The secret of your API KEY
 */
open class Cryptr(
    protected val tenantDomain: String = System.getProperty(CryptrEnvironment.CRYPTR_TENANT_DOMAIN.toString()),
    protected val baseUrl: String = System.getProperty(CryptrEnvironment.CRYPTR_BASE_URL.toString(), DEFAULT_BASE_URL),
    protected val defaultRedirectUrl: String = System.getProperty(
        CryptrEnvironment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    protected val apiKeyClientId: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    protected val apiKeyClientSecret: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
) {
    val format = Json { ignoreUnknownKeys = true }

    private val logger = KotlinLogging.logger {}

    init {
        setLogLevel(Level.INFO.toString())
        logInfo {
            """Cryptr intialized with:
            |- tenantDomain: $tenantDomain
            |- baseUrl: $baseUrl
            |- defaultRedirection: $defaultRedirectUrl
            |- apiKeyClientId: $apiKeyClientId
            |- apiKeyClientSecret: $apiKeyClientSecret
        """.trimMargin()
        }
    }

    private fun buildCryptrUrl(path: String): String {
        return if (path.startsWith("/")) "$baseUrl$path" else "$baseUrl/$path"
    }

    private fun mapToFormData(params: Map<String, Any?>): String? {
        return params
            .entries
            .stream()
            .map { p ->
//                println(p.key)
//                println(p.value?.javaClass.toString())
//                println(p.value?.javaClass.toString() == "class java.util.ArrayList")
                p.key + "=" + URLEncoder.encode(p.value.toString(), "utf-8")
            }
            .reduce { p1, p2 -> "$p1&$p2" }
            .map { s -> s }
            .orElse("")
    }

    protected fun makeRequest(
        path: String,
        params: Map<String, Any?>? = null,
        apiKeyToken: String? = ""
    ): JSONObject {
        try {
            val url = URL(buildCryptrUrl(path))
            val conn = url.openConnection() as HttpURLConnection

            conn.doOutput = true
            conn.useCaches = false
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("Accept", "application/json")
            if (apiKeyToken !== "" && apiKeyToken !== null) {
                conn.setRequestProperty("Authorization", "Bearer $apiKeyToken")
            }
            if (params != null) {
                val formData = mapToFormData(params)
                conn.setRequestProperty("Content-Length", formData?.length.toString())
                DataOutputStream(conn.outputStream).use { it.writeBytes(formData) }
            }

            BufferedReader(
                InputStreamReader(if (conn.responseCode > 299) conn.errorStream else conn.inputStream, "utf-8")
            ).use { br ->
                val response = StringBuilder()
                var responseLine: String?
                while (br.readLine().also { responseLine = it } != null) {
                    response.append(responseLine!!.trim { it <= ' ' })
                }
                try {
                    logDebug { response.toString() }
                    return JSONObject(response.toString())
                } catch (ej: JSONException) {
                    logException(ej)
                    return JSONObject().put("error", response.toString())
                }

            }
        } catch (e: Exception) {
            logException(e)
            return JSONObject().put("error", e.message)
        }
    }

    private fun currentLogger(): Logger {
        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val packageName = Cryptr::javaClass.name
        return loggerContext.getLogger(packageName)
    }


    protected fun logInfo(info: () -> Any?) {
        printCurrentLogLevel()
        val currentLogger = currentLogger()
        if (currentLogger.isInfoEnabled) {
            logger.info(info().toString())
        } else {
            logger.warn("sorry Info level not active")
        }
    }

    protected fun logDebug(debug: () -> Any?) {
        printCurrentLogLevel()
        val currentLogger = currentLogger()
        if (currentLogger.isInfoEnabled) {
            logger.debug(debug().toString())
        } else {
            logger.warn("Sorry Debug level is not active")
        }
    }

    protected fun logException(exception: java.lang.Exception) {
        printCurrentLogLevel()
        logger.error("an exception occured:\n$exception")
    }

    private fun printCurrentLogLevel() {
        val logger = currentLogger()
        println("current level + ${logger.level}")
    }

    fun setLogLevel(logLevel: String) {
        val logger = currentLogger()
        printCurrentLogLevel()
        logger.level = Level.toLevel(logLevel)
        printCurrentLogLevel()
    }

    fun retrieveApiKeyToken(): String? {
        if (System.getProperty("CRYPTR_API_KEY_TOKEN", "null") !== "null") {
            return System.getProperty("CRYPTR_API_KEY_TOKEN")
        } else if (System.getProperty("CRYPTR_CURRENT_API_TOKEN", "null") !== "null") {
            return System.getProperty("CRYPTR_CURRENT_API_TOKEN")
        } else {
            val params = mapOf(
                "client_id" to apiKeyClientId,
                "client_secret" to apiKeyClientSecret,
                "tenant_domain" to tenantDomain,
                "grant_type" to "client_credentials"
            )
            Constants.API_BASE_BATH
            val apiKeyTokenResponse =
                makeRequest(
                    "${Constants.API_BASE_BATH}/${Constants.API_VERSION}${CryptrApiPath.API_KEY_TOKEN.pathValue}",
                    params
                )
            val apiKeyToken = apiKeyTokenResponse.getString("access_token")
            if (apiKeyToken !== null) {
                System.setProperty("CRYPTR_CURRENT_API_KEY_TOKEN", apiKeyToken)
            }
            return apiKeyToken
        }
    }
}