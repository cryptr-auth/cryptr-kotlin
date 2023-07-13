package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PasswordChallenge(
    @SerialName("__type__") override val cryptrType: String = "PasswordChallenge",

    @SerialName("error") val error: String? = null,
    @SerialName("expired_at") val expiredAt: String? = null,
    @SerialName("renew_password") val renewPassword: String? = null,
    @SerialName("request_id") val requestId: String,
    @SerialName("valid?") val isValid: Boolean = false,
    @SerialName("code") val code: String? = null,

    ) : CryptrResource() {

    fun isSuccess(): Boolean {
        return isValid;
    }
}