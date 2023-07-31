package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an existing [Password] for a [User]
 */
@Serializable
class Password(
    /**
     * Cryptr resource type. SHOULD be "Password"
     */
    @SerialName("__type__") override val cryptrType: String = "Password",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    /**
     * String value to allow update of Password's plainText
     */
    @SerialName("code") val passwordCode: String? = null,
    /**
     * Unique identifier of the [User] owning this Password
     */
    @SerialName("user_id") val userId: String? = null,
    /**
     * Unique identifier of the Password resource
     */
    @SerialName("id") val id: Int? = null,

    ) : CryptrResource()