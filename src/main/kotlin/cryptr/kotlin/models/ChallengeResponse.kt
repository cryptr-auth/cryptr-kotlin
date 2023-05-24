package cryptr.kotlin.models

import cryptr.kotlin.interfaces.Tokenable
import cryptr.kotlin.models.jwt.JWTToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a successful response of (SSO) Challenge Headless Authenticatino process
 */
@Serializable
data class ChallengeResponse(
    /**
     * The JWT Access Token of the authenticated end-user
     */
    @SerialName("access_token") val accessToken: String? = null,
    /**
     * The JWT ID Token of the authenticated end-user
     */
    @SerialName("id_token") val idToken: String? = null,

    @SerialName("refresh_retry") val refreshRetry: Int?,
    @SerialName("refresh_leeway") val refreshLeeway: Int?,
    /**
     * Number of refresh made on these JWT tokens
     */
    @SerialName("refresh_count") val refreshCount: Int?,

    /**
     * Associated oauth authorization unique identifier
     */
    @SerialName("oauth_authorization_id") val oauthAuthorizationId: String?,
    /**
     * Unique identifier of the resource owner of these tokens
     */
    @SerialName("resource_owner_id") val resourceOwnerId: String?,
    /**
     * Unique identifier of the Client ([Application])
     */
    @SerialName("client_id") val clientId: String?,
    /**
     * URL of the client
     */
    @SerialName("client_url") val clientUrl: String?,
    @SerialName("nonce") val nonce: String? = null,
    /** TEMP comment need to rollback asap **/
//    @SerialName("nonce") val nonce: String?,
    /**
     * Authorized scope of these generated tokens
     */
    @SerialName("scope") val scope: Set<String>? = setOf(),
    /**
     * SSO assertions of these tokens
     */
    @SerialName("assertion") val assertion: Map<String, String> = mapOf<String, String>(),
    /**
     * Type of token
     */
    @SerialName("token_type") val tokenType: String?,

    /**
     * Expiration date as [String]
     */
    @SerialName("expires_at") val expiresAt: String?,
    /**
     * Refresh expiration date (as [String])
     */
    @SerialName("refresh_token_expires_at") val refreshTokenExpiresAt: String?,
) : Tokenable {
    /**
     * Decode ID Token and retrieve its [JWTToken] if its valid
     *
     * @see idToken
     */
    fun getIdClaims(serviceUrl: String): JWTToken? {
        val token: String = idToken.toString()
        return verify(serviceUrl, token)
    }

    /**
     * Decode Access Token and retrieve its [JWTToken] if its valid
     *
     * @see accessToken
     */
    fun getAccessClaims(serviceUrl: String): JWTToken? {
        val token: String = accessToken.toString()
        return verify(serviceUrl, token)
    }
}
