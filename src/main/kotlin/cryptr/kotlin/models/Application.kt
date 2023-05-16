package cryptr.kotlin.models

import cryptr.kotlin.enums.ApplicationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Application(
    @SerialName("__type__") override val cryptrType: String = "Application",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
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
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "applications"
    }
}
