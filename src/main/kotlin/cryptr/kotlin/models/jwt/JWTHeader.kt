package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The header representation of a JWT token
 */
@Serializable
@SerialName("header")
data class JWTHeader(
    /**
     * Key ID that generated the current [JWTToken]
     */
    @SerialName("kid") val kid: String,
    /**
     * Issuer that generated the current [JWTToken]
     */
    @SerialName("iss") val iss: String,
    /**
     * Type of the current token.
     * MUST be `JWT`
     */
    @SerialName("typ") val typ: String,

    /**
     * Signing algorithm used for the generation of current [JWTToken].
     * Default is `RS256`
     */
    @SerialName("alg") val alg: String
) {

    init {
        require(kid.isNotEmpty() && kid.isNotBlank()) { "kid not compliant" }
        require(iss.isNotEmpty() && iss.isNotBlank()) { "iss not compliant" }
        require(typ == "JWT") { "require JWT typ" }
        val expectedAlg = System.getProperty("CRYPTR_JWT_ALG", "RS256")
        require(alg == expectedAlg) { "only '$expectedAlg' alg value supported" }
    }

}
