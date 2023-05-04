package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SSOChallenge(
    val database: String,
    @SerialName("saml_idp_id") val samlIdpId: String,
    @SerialName("authorization_url") val authorizationUrl: String,
    @SerialName("expired_at") val expiredAt: Int,
    @SerialName("redirect_uri") val redirectUri: String,
    @SerialName("request_id") val requestId: String
) {
}