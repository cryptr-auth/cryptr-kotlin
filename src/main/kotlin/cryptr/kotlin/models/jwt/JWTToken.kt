package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation of a JWT Token
 */
@Serializable
data class JWTToken(
    /**
     * Represents the JWT Token Header
     */
    @SerialName("header") val header: JWTHeader,

    /**
     * Represents the JWT Token claims
     */
    @SerialName("payload") val payload: JWTPayload,
) {

    /**
     * ISS Verification need to be made through [JWTToken.verifyIss]
     */
    var validIss = false

    /**
     * Verify Issuer value (`iss`) from both header and payload
     *
     * @param serviceUrl Cryptr Service URL to refer to for verification
     * @param forceIss (Optional) [Boolean] to bypass ISS check (not recommended)
     *
     * @return [Boolean] result of the iss validation (same value in payload and header
     * and matching both `serviceUrl` and `tnt` claims value
     */
    fun verifyIss(serviceUrl: String, forceIss: Boolean? = false): JWTToken {
        val tnt = payload.tnt
        val issValues = setOf(header.iss, payload.iss)
        val compatibleIss = issValues.all { it.startsWith(serviceUrl) && it.endsWith(tnt) }
        this.validIss = (forceIss == true) || compatibleIss
        return this
    }

    /**
     * Returns JWT Token Claims
     *
     * @return [JWTPayload]
     *
     * @see payload
     */
    fun claims(): JWTPayload {
        return payload
    }
}
