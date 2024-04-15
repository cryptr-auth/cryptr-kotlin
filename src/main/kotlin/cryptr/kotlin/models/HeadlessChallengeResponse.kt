package cryptr.kotlin.models

import cryptr.kotlin.interfaces.Tokenable
import cryptr.kotlin.models.jwt.JWTToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a successful response of (SSO) Challenge Headless Authenticatino process
 */
@Serializable
data class HeadlessChallengeResponse(
    /**
     * The JWT Access Token of the authenticated end-user
     */
    @SerialName("access_token") val accessToken: String? = null,
    /**
     * The JWT ID Token of the authenticated end-user
     */
    @SerialName("id_token") val idToken: String? = null,
    /**
     * The Refresh Token to rotate the session
     */
    @SerialName("refresh_token") val refreshToken: String? = null,

    /**
     * Authorized scope of these generated tokens
     */
    @SerialName("scope") val scope: Set<String>? = setOf(),
    @SerialName("token_type") val tokenType: String? = "Bearer",

    /**
     * Expiration date as [String]
     */
    @SerialName("expires_in") val expiresIn: Int? = null,
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
