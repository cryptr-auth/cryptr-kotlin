package cryptr.kotlin.models.connections

import cryptr.kotlin.models.AdminOnboarding
import cryptr.kotlin.models.CryptrResource
import cryptr.kotlin.models.Redirection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SSOConnection(
    @SerialName("__type__") override val cryptrType: String = "SSOConnection",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    @SerialName("id") val id: String? = null,
    @SerialName("sp_id") val spId: String? = null,
    @SerialName("active") val active: Boolean = true,
    @SerialName("default_redirection") val defaultRedirection: Redirection? = null,
    @SerialName("metadata") val metadata: CryptrResource? = null,
    @SerialName("provider_type") val providerType: String? = null,
    @SerialName("user_provider_type") val userProviderType: String? = null,
    @SerialName("seats_limit") val seatsLimit: Int? = null,
    @SerialName("number_user_provisioning_limit") val numberUserProvisioningLimit: Int? = null,
    @SerialName("users_provisioning_on_first_login") val usersProvisioningOnFirstLogin: Int? = null,

    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("inserted_at") val insertedAt: String? = null,

    @SerialName("onboarding") val onboarding: AdminOnboarding?
) : CryptrResource()
