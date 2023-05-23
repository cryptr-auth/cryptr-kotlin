package cryptr.kotlin.enums

import cryptr.kotlin.Cryptr
import cryptr.kotlin.models.SSOChallenge
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types for [SSOChallenge]
 *
 *
 * @see Cryptr.createSsoChallenge(authType = ChallengeType.SAML)
 */
@Serializable
enum class ChallengeType(val value: String) {
    /**
     * FOR SAML Authentication process
     */
    @SerialName("saml")
    SAML("saml"),

    /**
     * FOR OAuth Authentication process
     */
    @SerialName("oauth")
    OAUTH("oauth")
}