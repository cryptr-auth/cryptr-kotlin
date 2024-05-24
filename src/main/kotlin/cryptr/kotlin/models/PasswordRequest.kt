package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the response of a password request
 */
@Serializable
class PasswordRequest(
    /**
     * Cryptr Resource type SHOULD be "PasswordRequest"
     */
    @SerialName("__type__") override val cryptrType: String = "PasswordRequest",
    @SerialName("__domain__") override val resourceDomain: String? = null,
    @SerialName("__environment__") override val environment: String? = null,

    /**
     * Unique identifier of the user
     */
    @SerialName("user_id") val userId: String? = null,
    /**
     * Magic link to give to end-user to handle the password update
     */
    @SerialName("magic_link") val magicLink: String? = null,
    /**
     * The redirect uri chosen that will be used after password authentication
     */
    @SerialName("redirect_uri") val redirectUri: String? = null,
    /**
     * The unique identifier of the request
     */
    @SerialName("request_id") val requestId: String? = null,
    /**
     * The expiration date of the request
     */
    @SerialName("expired_at") val expiredAt: String? = null,
) : CryptrResource()