package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeResponse(
    @SerialName("access_token") val accessToken: String?,
    @SerialName("id_token") val idToken: String?,

    @SerialName("refresh_retry") val refreshRetry: String?,
    @SerialName("refresh_leeway") val refreshLeeway: String?,
    @SerialName("refresh_count") val refreshCount: String?,

    @SerialName("oauth_authorization_id") val oauthAuthorizationId: String?,
    @SerialName("resource_owner_id") val resourceOwnerId: String?,
    @SerialName("client_id") val clientId: String?,
    @SerialName("client_url") val clientUrl: String?,
    @SerialName("nonce") val nonce: String?,
    @SerialName("scope") val scope: String?,
    @SerialName("assertion") val assertion: String?,
    @SerialName("token_type") val tokenType: String?,

    @SerialName("expires_at") val expiresAt: String?,
    @SerialName("refresh_token_expires_at") val refreshTokenExpiresAt: String?,
)
