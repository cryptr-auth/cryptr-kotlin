package cryptr.kotlin.models.connections

import cryptr.kotlin.models.CryptrResource
import cryptr.kotlin.models.Organization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Password connection
 */
@Serializable
data class PasswordConnection(
    /**
     * Cryptr Type = "PasswordConnection"
     */
    @SerialName("__type__") override val cryptrType: String = "PasswordConnection",
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
     * Period duration for the pepper rotation
     */
    @SerialName("pepper_rotation_period") val pepperRotationPeriod: Int? = null,
    /**
     * Minimum count of characters for the password string value
     */
    @SerialName("plain_text_min_length") val plainTextMinLength: Int? = null,
    /**
     * Maximum count of characters for the password string value
     */
    @SerialName("plain_text_max_length") val plainTextMaxLength: Int? = null,
    /**
     * The unique identifier of the email template for the forgot password process
     */
    @SerialName("forgot_password_template_id") val forgotPasswordTemplateId: String? = null,

    /**
     * Last update date of the resource
     */
    @SerialName("updated_at") val updatedAt: String? = null,
    /**
     * Creation date of the resource
     */
    @SerialName("inserted_at") val insertedAt: String? = null,

    ) : CryptrResource()