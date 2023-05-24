package cryptr.kotlin.models.connections

import cryptr.kotlin.models.AdminOnboarding
import cryptr.kotlin.models.CryptrResource
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.Redirection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Represents the SSO Configuration for an [Organization]
 */
@Serializable
data class SSOConnection(
    /**
     * Cryptr Type = "SSOConnection"
     */
    @SerialName("__type__") override val cryptrType: String = "SSOConnection",
    @SerialName("__environment__") override val environment: String? = null,

    /**
     * [Organization]'s domain to whom the SSOConnection is associated to
     */
    @SerialName("__domain__") override val resourceDomain: String? = null,

    /**
     * The Unique Identifier of the SSoConnection, ex: `organization_domain_xeabxxxx`
     */
    @SerialName("id") val id: String? = null,
    /**
     * The Unique Service provider Identifier of the SSoConnection, ex: `my_company_xeabxxxx`
     */
    @SerialName("sp_id") val spId: String? = null,
    /**
     * Represent if the SSOConnection is enabled or not
     */
    @SerialName("active") val active: Boolean = true,
    /**
     * The Default [Redirection] used after the end-user succeeded his SSO authnetication process
     */
    @SerialName("default_redirection") val defaultRedirection: Redirection? = null,
    /**
     * @suppress
     */
    @SerialName("metadata") val metadata: CryptrResource? = null,
    /**
     * The provider type of the SSO Connection, ex: `okta`, `google`, `adfs` ...
     */
    @SerialName("provider_type") val providerType: String? = null,
    /**
     * What rule to use for end-user coming from the SSO Connection:
     *
     * 1. Default: `none` : All users coming from SSO can open a valid session
     * 2. `user_provisionned` : Only user present in the Organization evnironment database can connect
     * 3. Define a seats_limit that cannot be exceeded
     *
     * @see [SSOConnection.seatsLimit]
     */
    @SerialName("user_provider_type") val userProviderType: String? = null,
    /**
     * Define a limit of users that cannot be overflowed. Any new user has to wait a deletion of another one
     */
    @SerialName("seats_limit") val seatsLimit: Int? = null,
    /**
     * @see seatsLimit
     */
    @SerialName("number_user_provisioning_limit") val numberUserProvisioningLimit: Int? = null,
    /**
     * @see userProviderType
     */
    @SerialName("users_provisioning_on_first_login") val usersProvisioningOnFirstLogin: Int? = null,

    /**
     * [String] value of last update date
     */
    @SerialName("updated_at") val updatedAt: String? = null,
    /**
     * [String] value of creation date
     */
    @SerialName("inserted_at") val insertedAt: String? = null,

    /**
     * Associated [AdminOnboarding] To follow and allow organization's IT admin to configure current SSOConnection
     */
    @SerialName("onboarding") val onboarding: AdminOnboarding?
) : CryptrResource()
