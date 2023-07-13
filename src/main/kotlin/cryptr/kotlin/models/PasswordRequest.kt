package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PasswordRequest(
    @SerialName("__domain__") override val resourceDomain: String? = null,
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__type__") override val cryptrType: String = "PasswordRequest",

    @SerialName("user_id") val userId: String? = null,
    @SerialName("magic_link") val magicLink: String? = null,
    @SerialName("redirect_uri") val redirectUri: String? = null,
    @SerialName("request_id") val requestId: String? = null,

    @SerialName("expired_at") val expiredAt: String? = null,
) : CryptrResource()