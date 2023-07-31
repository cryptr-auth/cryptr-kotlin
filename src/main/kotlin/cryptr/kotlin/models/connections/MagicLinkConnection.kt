package cryptr.kotlin.models.connections

import cryptr.kotlin.models.CryptrResource
import cryptr.kotlin.models.Organization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Magic link Connection
 */
@Serializable
data class MagicLinkConnection(
    /**
     * Cryptr Type = "PasswordConnection"
     */
    @SerialName("__type__") override val cryptrType: String = "MagicLinkConnection",
    @SerialName("__environment__") override val environment: String? = null,

    /**
     * [Organization]'s domain to whom the SSOConnection is associated to
     */
    @SerialName("__domain__") override val resourceDomain: String? = null,

    /**
     * Unique identifier of the resource
     */
    @SerialName("id") val id: String? = null,

    /**
     * Unique identifier of the template to use to send magic link
     */
    @SerialName("sign_in_template_id") val signInTemplateId: String? = null,

    /**
     * If the user has to be registered to connect or not
     *
     * `true` Means that a User has to exist in User Registry with given email
     * `false` Means that user will be automatically created if not exists (after link clicked)
     */
    @SerialName("fin_or_create_user") val findOrCreateUser: Boolean? = false,

    /**
     * Last update date of the resource
     */
    @SerialName("updated_at") val updatedAt: String? = null,
    /**
     * Creation date of the resource
     */
    @SerialName("inserted_at") val insertedAt: String? = null,
) : CryptrResource()