package cryptr.kotlin.models.connections

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Helpful information about SAML Configuration of SSO Connection
 */
@Serializable
@SerialName("saml_config")
data class SAMLConfig(
    /**
     * ACS URL (Assertion Consumer Service URL)
     */
    @SerialName("acs_url") val acsUrl: String? = null,
    /**
     * Metadata URL of the Service provider
     */
    @SerialName("cryptr_metadata_url") val cryptrMetadataUrl: String? = null,
    /**
     * Entity ID of the SSO Connection
     */
    @SerialName("entity_id") val entityId: String? = null,
    /**
     * SLO Response URL
     */
    @SerialName("slo_response_url") val sloRepsonseUrl: String? = null,
    /**
     * SLO URL
     */
    @SerialName("slo_url") val sloUrl: String? = null,
    /**
     * SSO Provider Metadata
     */
    @SerialName("sso_provider_metadata") val ssoProviderMetadata: String? = null,
    /**
     * Version of umbrella used when configured (0, or 1)
     */
    @SerialName("version") val version: Int? = null,
)
