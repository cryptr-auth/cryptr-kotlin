package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeResponse(@SerialName("access_token") val accessToken: String?) {
}
