package cryptr.kotlin.models

import cryptr.kotlin.interfaces.Tokenable
import cryptr.kotlin.models.jwt.JWTPayload
import cryptr.kotlin.models.jwt.JWTToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a successful response of (SSO) Challenge Headless Authentication process
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
    /**
     * The Refresh Token of the authenticated end-user
     */
    @SerialName("refresh_token") val refreshToken: String? = null,

    /**
     * Authorized scope of these generated tokens
     */
    @SerialName("scope") val scope: Set<String>? = setOf(),
    /**
     * Type of token
     */
    @SerialName("token_type") val tokenType: String? = null,

    /**
     * Expiration in  duration as [Long] seconds
     */
    @SerialName("expires_in") val expiresIn: Long? = null,
) : Tokenable {
    /**
     * Decode ID Token and retrieve its [JWTToken] if its valid
     *
     * @see idToken
     */
    fun getIdToken(serviceUrl: String): JWTToken? {
        val token: String = idToken.toString()
        return verify(serviceUrl, token, false)
    }

    fun getIdClaims(serviceUrl: String): JWTPayload? {
        return getIdToken(serviceUrl)?.claims()
    }

    /**
     * Decode Access Token and retrieve its [JWTToken] if its valid
     *
     * @see accessToken
     */
    fun getAccessToken(serviceUrl: String): JWTToken? {
        val token: String = accessToken.toString()
        return verify(serviceUrl, token)
    }

    fun getAccessClaims(serviceUrl: String): JWTPayload? {
        return getAccessToken(serviceUrl)?.claims()
    }
}
