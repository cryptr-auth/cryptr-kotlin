package cryptr.kotlin.models.connections

import cryptr.kotlin.enums.UserProvisioning
import cryptr.kotlin.models.CryptrResource
import cryptr.kotlin.models.Organization
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
     * Represent if the SSOConnection is enabled or not
     */
    @SerialName("active") val active: Boolean? = null,
    /**
     * The provider type of the SSO Connection, ex: `okta`, `google`, `adfs` ...
     */
    @SerialName("provider_type") val providerType: String? = null,
    @SerialName("number_user_provisioning_limit") val numberUserProvisioningLimit: Int? = null,
    /**
     * @see userProviderType
     */
    @SerialName("users_provisioning_on_first_login") val usersProvisioningOnFirstLogin: UserProvisioning? = null,

    /**
     * [String] value of last update date
     */
    @SerialName("updated_at") val updatedAt: String? = null,
    /**
     * [String] value of creation date
     */
    @SerialName("inserted_at") val insertedAt: String? = null,

    /**
     * SAML Configuration
     * @since 0.1.3
     */
    @SerialName("saml_config") val samlConfig: SAMLConfig? = null,
    /**
     * Associated [Organization]
     */
    val organization: Organization? = null,

    ) : CryptrResource() {
    init {
        require(numberUserProvisioningLimit == null || numberUserProvisioningLimit > 0)
    }
}
