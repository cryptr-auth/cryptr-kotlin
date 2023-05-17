package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JWTToken(
    @SerialName("header") val header: JWTHeader,
    @SerialName("payload") val payload: JWTPayload,
) {

    var validIss = false

    fun verifyIss(baseUrl: String, forceIss: Boolean? = false): JWTToken {
        val tnt = payload.tnt
        val issValues = setOf(header.iss, payload.iss)
        val compatibleIss = issValues.all { it.startsWith(baseUrl) && it.endsWith(tnt) }
        this.validIss = (forceIss == true) || compatibleIss
        return this
    }

    fun claims(): JWTPayload {
        return payload
    }
}
