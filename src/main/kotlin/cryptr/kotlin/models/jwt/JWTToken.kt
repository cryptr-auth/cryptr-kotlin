package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JWTToken(
    @SerialName("header") val header: JWTHeader,
    @SerialName("payload") val payload: JWTPayload,
) {

    var validIss = false

    fun verifyIss(baseUrl: String): JWTToken {
        this.validIss = header.iss.startsWith(baseUrl) && payload.iss.startsWith(baseUrl)
        return this
    }

    fun claims(): JWTPayload {
        return payload
    }
}
