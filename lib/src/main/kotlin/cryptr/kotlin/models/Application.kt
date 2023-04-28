package cryptr.kotlin.models

import org.json.JSONObject

data class Application(
    val id: String? = null,
    val clientId: String? = null,
    val name: String,
    val applicationType: String,
    val defaultOriginCors: String? = null,
    val defaultRedirectUriAfterLogout: String? = null,
    val defaultRedirectUriAfterLogin: String? = null,
    val description: String? = null,
    val allowedOriginsCors: Any? = null,
    val allowedRedirectUrls: Any? = null,
    val allowedLogoutUrls: Any? = null,
    val updatedAt: String? = null,
    val insertedAt: String? = null
) {
    companion object {
        const val apiResourceName: String = "applications"
    }

    constructor(jsonObject: JSONObject) : this(
        jsonObject.optString("id"),
        jsonObject.optString("client_id"),
        jsonObject.getString("name"),
        jsonObject.getString("application_type"),
        jsonObject.optString("default_origin_cors"),
        jsonObject.optString("default_redirect_uri_after_logout"),
        jsonObject.optString("default_redirect_uri_after_login"),
        jsonObject.optString("description"),
        jsonObject.opt("allowed_origins_cors"),
        jsonObject.opt("allowed_redirect_urls"),
        jsonObject.opt("allowed_logout_urls"),
        jsonObject.optString("updated_at"),
        jsonObject.optString("inserted_at")
    )
}
