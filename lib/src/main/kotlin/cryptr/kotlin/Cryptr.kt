package cryptr.kotlin

import ch.qos.logback.classic.Level
import cryptr.kotlin.enums.CryptrApiPath
import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.interfaces.Loggable
import cryptr.kotlin.interfaces.URLable
import cryptr.kotlin.objects.Constants
import io.github.oshai.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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
) : URLable, Loggable {
    @OptIn(ExperimentalSerializationApi::class)
    val format = Json { ignoreUnknownKeys = true; explicitNulls = true; encodeDefaults = true }
    
    init {
        if (!isJUnitTest()) {
            setLogLevel(Level.INFO.toString())
        }
        logInfo({
            """Cryptr intialized with:
                |- tenantDomain: $tenantDomain
                |- baseUrl: $baseUrl
                |- defaultRedirection: $defaultRedirectUrl
                |- apiKeyClientId: $apiKeyClientId
                |- apiKeyClientSecret: $apiKeyClientSecret
            """.trimMargin()
        })
    }

    private fun buildCryptrUrl(path: String): String {
        return buildCryptrUrl(baseUrl, path)
    }

    protected fun makeDeleteRequest(path: String, apiKeyToken: String? = ""): JSONObject {
        return makeRequest(path = path, apiKeyToken = apiKeyToken, requestMethod = "DELETE")
    }

    protected fun makeUpdateRequest(
        path: String,
        params: Map<String, Any?>? = null,
        apiKeyToken: String? = ""
    ): JSONObject {
        return makeRequest(path, params, apiKeyToken, "PUT")
    }

    protected fun makeRequest(
        path: String,
        params: Map<String, Any?>? = null,
        apiKeyToken: String? = "",
        requestMethod: String? = null
    ): JSONObject {
        try {
            val url = URL(buildCryptrUrl(path))
            val conn = url.openConnection() as HttpURLConnection

            logDebug(debug = { url.toString() })

            conn.doOutput = true
            conn.useCaches = false
            if (requestMethod !== null) {
                conn.requestMethod = requestMethod
            }
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("Accept", "application/json")
            if (apiKeyToken !== "" && apiKeyToken !== null) {
                conn.setRequestProperty("Authorization", "Bearer $apiKeyToken")
            }
            if (params != null) {
                val formData = mapToFormData(params)
                conn.setRequestProperty("Content-Length", formData.length.toString())
                logDebug({ formData.toString() })
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
                    if (!url.toString().contains("token")) logDebug({ response.toString() })
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