package cryptr.kotlin.models

import cryptr.kotlin.enums.ApplicationType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent a front [Application]
 */
@Serializable
data class Application(
    /**
     * Cryptr Resource type. SHOULD be "Application"
     */
    @SerialName("__type__") override val cryptrType: String = "Application",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
    /**
     * Friendly name of the Application
     */
    val name: String,
    /**
     * Type of Application
     * @see [ApplicationType]
     */
    @SerialName("application_type") val applicationType: ApplicationType,
    /**
     * Unique identifier of [Application]
     */
    val id: String? = null,
    /**
     * Unique identifier of [Application]
     * @see id
     */
    @SerialName("client_id") val clientId: String? = null,
    /**
     * Default origin CORS
     */
    @SerialName("default_origin_cors") val defaultOriginCors: String? = null,
    /**
     * Default URL where to redirect user after logout
     */
    @SerialName("default_redirect_uri_after_logout") val defaultRedirectUriAfterLogout: String? = null,
    /**
     * Default URL where to redirect user after login
     */
    @SerialName("default_redirect_uri_after_login") val defaultRedirectUriAfterLogin: String? = null,
    /**
     * Description text explaining purpose of application
     */
    val description: String? = null,
    /**
     * List all allowed origin cors ([String] URLs)
     */
    @SerialName("allowed_origins_cors") val allowedOriginsCors: Set<String>? = null,
    /**
     * List all allowed redirect URLs ([String] list) for this app
     */
    @SerialName("allowed_redirect_urls") val allowedRedirectUrls: Set<String>? = null,
    /**
     * List all allowed redirect URLs after logout ([String] list) for this app
     */
    @SerialName("allowed_logout_urls") val allowedLogoutUrls: Set<String>? = null,
    /**
     * Date of last update as [String]
     */
    @SerialName("updated_at") val updatedAt: String? = null,
    /**
     * Date of creation as [String]
     */
    @SerialName("inserted_at") val insertedAt: String? = null
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "applications"
    }
}
