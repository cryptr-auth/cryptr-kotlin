package cryptr.kotlin.interfaces

import cryptr.kotlin.models.jwt.JWTToken
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.util.*

interface Tokenable : Loggable {

    fun verify(baseUrl: String, token: String, forceIss: Boolean? = true): JWTToken? {
        return try {
            val decoded = decodeToken(token)
            val sanitized: String = sanitize(decoded)
            val jwtToken = Json{ignoreUnknownKeys = true}.decodeFromString<JWTToken>(sanitized)
            jwtToken.verifyIss(baseUrl, forceIss)
        } catch (e: Exception) {
            logException(e)
            println(e.message)
            null
        }

    }

    private fun sanitize(decoded: JSONObject): String {
        return try {
            val payload = decoded.getJSONObject("payload")
            if (payload.get("scp") is String) {
                payload.put("scp", payload.getString("scp").split(" "))
            }
            decoded.put("payload", payload)
            decoded.toString()
        } catch (e: Exception) {
            logException(e)
            "{}"
        }
    }

    private fun decodeToken(token: String): JSONObject {
        val chunks = token.split(".")

        if (chunks.size < 2) throw Exception("wrong input token, given '$token'")
        return JSONObject()
            .put("header", decodeTokenChunk(chunks[0]))
            .put("payload", decodeTokenChunk(chunks[1]))
    }

    private fun decodeTokenChunk(chunk: String): JSONObject {
        val decoder = Base64.getUrlDecoder()
        return JSONObject(String(decoder.decode(chunk)))
    }
}