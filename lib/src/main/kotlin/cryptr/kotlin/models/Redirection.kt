package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Redirection")
data class Redirection(
    @SerialName("__type__") override val cryptrType: String = "SsoConnection",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    @SerialName("id") val id: String? = null,
    @SerialName("app_id") val applicationId: String? = null,
    @SerialName("idp_id") val idpId: String? = null,
    @SerialName("sp_id") val spId: String? = null,
    @SerialName("uri") val uri: String? = null,
    @SerialName("app_tenant_owner_id") val applicationTenantOwnerId: String? = null,
) : CryptrResource()
