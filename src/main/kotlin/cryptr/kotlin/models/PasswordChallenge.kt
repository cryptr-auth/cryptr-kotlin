package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to handle password renewal
 */
@Serializable
class RenewPassword(
    /**
     * Code to allow password renewal
     */
    @SerialName("password_code") val passwordCode: String
) {}

/**
 * Response of the password authentication process
 */
@Serializable
class PasswordChallenge(
    /**
     * Cryptr resource type. SHOULD be "PasswordChallenge"
     */
    @SerialName("__type__") override val cryptrType: String = "PasswordChallenge",
    /**
     * Reason why the [PasswordChallenge] failed
     */
    @SerialName("error") val error: String? = null,
    /**
     * Date as [String] of the expiration of this [PasswordChallenge]
     */
    @SerialName("expired_at") val expiredAt: String? = null,
    /**
     * If password is expired and you have to renew the password
     */
    @SerialName("renew_password") val renewPassword: RenewPassword? = null,
    /**
     * Unique identifier of the request
     */
    @SerialName("request_id") val requestId: String,
    /**
     * is the [PasswordChallenge] valid
     */
    @SerialName("valid?") val isValid: Boolean = false,
    /**
     * String code to retrieve tokens
     */
    @SerialName("code") val code: String? = null,

    ) : CryptrResource() {

    /**
     * Returns if PasswordChallenge is a success
     * @return isValid as [Boolean]
     */
    fun isSuccess(): Boolean {
        return isValid;
    }

    /**
     * Returns if the PasswordChallenge is expired or not. Returns true if [expiredAt] is not null
     * @return [Boolean] of non-null expiredAt
     */
    fun isExpired(): Boolean {
        return expiredAt !== null
    }

    /**
     * Returns passwordCode if exists
     * @return [RenewPassword] [passwordCode] [String] value
     */
    fun getRenewCode(): String {
        return renewPassword?.passwordCode!!
    }
}