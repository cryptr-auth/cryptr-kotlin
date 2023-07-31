package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MagicLinkChallenge(
    /**
     * Cryptr resource type. SHOULD be "PasswordChallenge"
     */
    @SerialName("__type__") override val cryptrType: String = "PasswordChallenge",

    /**
     * Unique identifier of the request
     */
    @SerialName("request_id") val requestId: String,

    /**
     * String code to retrieve tokens
     */
    @SerialName("code") val code: String? = null,
) : CryptrResource()