package cryptr.kotlin.models.connections

import cryptr.kotlin.models.CryptrResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PasswordConnection(
    @SerialName("__type__") override val cryptrType: String = "PasswordConnection",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    @SerialName("id") val id: String? = null,
    @SerialName("pepper_rotation_period") val pepperRotationPeriod: Int? = null,
    @SerialName("plain_text_min_length") val plainTextMinLength: Int? = null,
    @SerialName("plain_text_max_length") val plainTextMaxLength: Int? = null,
    @SerialName("forgot_password_template_id") val forgotPasswordTemplateId: String? = null,

    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("inserted_at") val insertedAt: String? = null,

    ) : CryptrResource()