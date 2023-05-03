package cryptr.kotlin.models

import cryptr.kotlin.enums.ApplicationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONObject

@Serializable
data class Application(
    val name: String,
    @SerialName("application_type") val applicationType: ApplicationType,
    val id: String? = null,
    @SerialName("client_id") val clientId: String? = null,
    @SerialName("default_origin_cors") val defaultOriginCors: String? = null,
    @SerialName("default_redirect_uri_after_logout") val defaultRedirectUriAfterLogout: String? = null,
    @SerialName("default_redirect_uri_after_login") val defaultRedirectUriAfterLogin: String? = null,
    val description: String? = null,
    // Set to avoid duplicate values
    @SerialName("allowed_origins_cors") val allowedOriginsCors: Set<String>? = null,
    @SerialName("allowed_redirect_urls") val allowedRedirectUrls: Set<String>? = null,
    @SerialName("allowed_logout_urls") val allowedLogoutUrls: Set<String>? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("inserted_at") val insertedAt: String? = null
) {
    companion object {
        const val apiResourceName: String = "applications"
    }

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
