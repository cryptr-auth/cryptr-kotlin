package cryptr.kotlin.models

import cryptr.kotlin.enums.ApplicationType
import org.json.JSONArray
import org.json.JSONObject

data class Application(
    val name: String,
    val applicationType: ApplicationType,
    val id: String? = null,
    val clientId: String? = null,
    val defaultOriginCors: String? = null,
    val defaultRedirectUriAfterLogout: String? = null,
    val defaultRedirectUriAfterLogin: String? = null,
    val description: String? = null,
    val allowedOriginsCors: JSONArray? = null,
    val allowedRedirectUrls: JSONArray? = null,
    val allowedLogoutUrls: JSONArray? = null,
    val updatedAt: String? = null,
    val insertedAt: String? = null
) {
    companion object {
        const val apiResourceName: String = "applications"
    }

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("name"),
        ApplicationType.values().first { it.type == jsonObject.getString("application_type") },
        jsonObject.optString("id"),
        jsonObject.optString("client_id"),
        jsonObject.optString("default_origin_cors"),
        jsonObject.optString("default_redirect_uri_after_logout"),
        jsonObject.optString("default_redirect_uri_after_login"),
        jsonObject.optString("description"),
        jsonObject.optJSONArray("allowed_origins_cors"),
        jsonObject.optJSONArray("allowed_redirect_urls"),
        jsonObject.optJSONArray("allowed_logout_urls"),
        jsonObject.optString("updated_at"),
        jsonObject.optString("inserted_at")
    )

    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("client_id", clientId)
        obj.put("name", name)
        obj.put("application_type", applicationType.type)
        obj.put("default_redirect_uri_after_logout", defaultRedirectUriAfterLogout)
        obj.put("default_redirect_uri_after_login", defaultRedirectUriAfterLogin)
        obj.put("description", description)
        obj.put("allowed_origins_cors[]", allowedOriginsCors)
        obj.put("allowed_redirect_urls[]", allowedRedirectUrls)
        obj.put("allowed_logout_urls[]", allowedLogoutUrls)
        obj.put("updated_at", updatedAt)
        obj.put("inserted_at", insertedAt)
        return obj
    }
}
