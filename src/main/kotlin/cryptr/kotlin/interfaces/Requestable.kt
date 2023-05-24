package cryptr.kotlin.interfaces

import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.objects.Constants
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * @suppress
 */
interface Requestable : URLable, Loggable {


    /**
     * @suppress
     */
    fun paginationQuery(
        perPage: Int? = 10,
        currentPage: Int? = 1
    ): String {
        val map = mutableMapOf("per_page" to perPage, "current_page" to currentPage)
        map.filter { (_, v) -> v !== null && v > 0 }
        return if (map.isEmpty()) "" else "?" + mapToFormData(map)
    }

    /**
     * @suppress
     */
    fun makeListRequest(
        path: String,
        serviceUrl: String = System.getProperty(
            CryptrEnvironment.CRYPTR_BASE_URL.toString(),
            Constants.DEFAULT_BASE_URL
        ),
        apiKeyToken: String? = "",
        perPage: Int? = 10,
        currentPage: Int? = 1,
    ): JSONObject {
        return makeRequest(
            path = path + paginationQuery(perPage, currentPage),
            serviceUrl = serviceUrl,
            apiKeyToken = apiKeyToken,
            requestMethod = "GET"
        )
    }

    /**
     * @suppress
     */
    fun makeDeleteRequest(
        path: String,
        serviceUrl: String = System.getProperty(
            CryptrEnvironment.CRYPTR_BASE_URL.toString(),
            Constants.DEFAULT_BASE_URL
        ),
        apiKeyToken: String? = ""
    ): JSONObject {
        return makeRequest(path = path, serviceUrl = serviceUrl, apiKeyToken = apiKeyToken, requestMethod = "DELETE")
    }

    /**
     * @suppress
     */
    fun makeUpdateRequest(
        path: String,
        serviceUrl: String = System.getProperty(
            CryptrEnvironment.CRYPTR_BASE_URL.toString(),
            Constants.DEFAULT_BASE_URL
        ),
        params: Map<String, Any?>? = null,
        apiKeyToken: String? = ""
    ): JSONObject {
        return makeRequest(path, serviceUrl, params = params, apiKeyToken = apiKeyToken, requestMethod = "PUT")
    }

    /**
     * @suppress
     */
    fun makeRequest(
        path: String,
        serviceUrl: String = System.getProperty(
            CryptrEnvironment.CRYPTR_BASE_URL.toString(),
            Constants.DEFAULT_BASE_URL
        ),
        params: Map<String, Any?>? = null,
        apiKeyToken: String? = "",
        requestMethod: String? = null,
    ): JSONObject {
        try {
            val url = URL(buildCryptrUrl(serviceUrl, path))
            val conn = url.openConnection() as HttpURLConnection
            logDebug(debug = { url.toString() })

            conn.doOutput = true
            conn.useCaches = false
            if (requestMethod !== null) {
                conn.requestMethod = requestMethod
                logDebug(
                    { conn.requestMethod.toString() }
                )
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
                    logError({ response.toString() })
                    return JSONObject().put("error", response.toString())
                }

            }
        } catch (e: Exception) {
            logException(e)
            return JSONObject().put("error", e.message)
        }
    }
}