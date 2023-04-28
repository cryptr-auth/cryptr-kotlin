package cryptr.kotlin.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject

data class SSOChallenge(
    val database: String,
    val samlIdpId: String,
    val authorizationUrl: String,
    val expiredAt: Int,
    val redirectUri: String,
    val requestId: String
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("database"),
        jsonObject.getString("saml_idp_id"),
        jsonObject.getString("authorization_url"),
        jsonObject.getInt("expired_at"),
        jsonObject.getString("redirect_uri"),
        jsonObject.getString("request_id")
    )

    fun toJSONObject(): JsonObject {
        return buildJsonObject {
            put("database", database)
            put("samlIdpId", samlIdpId)
            put("authorization_url", authorizationUrl)
            put("expired_at", expiredAt)
            put("redirect_uri", redirectUri)
            put("request_id", requestId)
        }
    }
}