package cryptr.kotlin.models

import cryptr.kotlin.interfaces.Tokenable
import cryptr.kotlin.models.jwt.JWTToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("id_token") val idToken: String? = null,

    @SerialName("refresh_retry") val refreshRetry: Int?,
    @SerialName("refresh_leeway") val refreshLeeway: Int?,
    @SerialName("refresh_count") val refreshCount: Int?,

    @SerialName("oauth_authorization_id") val oauthAuthorizationId: String?,
    @SerialName("resource_owner_id") val resourceOwnerId: String?,
    @SerialName("client_id") val clientId: String?,
    @SerialName("client_url") val clientUrl: String?,
    @SerialName("nonce") val nonce: Set<String>? = setOf(),
    /** TEMP comment need to rollback asap **/
//    @SerialName("nonce") val nonce: String?,
    @SerialName("scope") val scope: Set<String>? = setOf(),
    @SerialName("assertion") val assertion: Map<String, String> = mapOf<String, String>(),
    @SerialName("token_type") val tokenType: String?,

    @SerialName("expires_at") val expiresAt: String?,
    @SerialName("refresh_token_expires_at") val refreshTokenExpiresAt: String?,
) : Tokenable {
    fun getIdClaims(baseUrl: String): JWTToken? {
        val token: String = idToken.toString()
        return verify(baseUrl, token)
    }

    fun getAccessClaims(baseUrl: String): JWTToken? {
        val token: String = accessToken.toString()
        return verify(baseUrl, token)
    }
}
