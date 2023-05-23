package cryptr.kotlin.models

import cryptr.kotlin.Cryptr
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response of the [Cryptr.createSSOChallenge] when succeeded
 */
@Serializable
data class SSOChallenge(
    val database: String,
    /**
     * Unique identifier of the [SSOConnection]
     */
    @SerialName("saml_idp_id") val samlIdpId: String,
    /**
     * URL to give to your end-user to complete his authentication process.
     * Either by a specific canal (SMS, email ..) or through a redirection
     */
    @SerialName("authorization_url") val authorizationUrl: String,
    /**
     * Expiration date of the authorization_url
     */
    @SerialName("expired_at") val expiredAt: Int,
    /**
     * Chosen redirect URI given in [Cryptr.createSSOChallenge]
     */
    @SerialName("redirect_uri") val redirectUri: String,
    /**
     * Unique identifier of the Challenge request
     */
    @SerialName("request_id") val requestId: String
) {
}