package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MagicLinkChallenge(
    /**
     * Cryptr resource type. SHOULD be "MagicLinkChallenge"
     */
    @SerialName("__type__") override val cryptrType: String = "MagicLinkChallenge",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    /**
     * Unique identifier of the request
     */
    @SerialName("request_id") val requestId: String,
    /**
     * String code to retrieve tokens
     */
    @SerialName("code") val code: String? = null,
    @SerialName("magic_link") val magicLink: String? = null,
    @SerialName("email_address") val emailAddress: String? = null,

    @SerialName("find_or_create_user") val findOrCreateUser: Boolean? = null,
) : CryptrResource()