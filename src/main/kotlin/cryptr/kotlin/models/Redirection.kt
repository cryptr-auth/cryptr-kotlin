package cryptr.kotlin.models

import cryptr.kotlin.models.connections.SSOConnection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Redirection configuration after successful authentication of a [SSOConnection]
 */
@Serializable
@SerialName("Redirection")
data class Redirection(
    @SerialName("__type__") override val cryptrType: String = "Redirection",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    /**
     * Unique identifier of the Redirection
     */
    @SerialName("id") val id: String? = null,
    /**
     * Unique identifier of the [SSOConnection]
     */
    @SerialName("idp_id") val idpId: String? = null,
    /**
     * Unique identifier of the Service provider which [SSOConnection] is attached to
     */
    @SerialName("sp_id") val spId: String? = null,
    /**
     * URI where to redirect after login
     */
    @SerialName("uri") val uri: String? = null,
    /**
     * Unique identifier owner where to redirect. Can be either your master tenant account or
     * the Organization's
     */
    @SerialName("app_tenant_owner_id") val applicationTenantOwnerId: String? = null,
) : CryptrResource()
