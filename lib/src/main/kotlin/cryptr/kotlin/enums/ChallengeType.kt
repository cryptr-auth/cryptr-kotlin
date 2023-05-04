package cryptr.kotlin.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ChallengeType(val value: String) {
    @SerialName("saml")
    SAML("saml"),
    @SerialName("oauth")
    OAUTH("oauth")
}