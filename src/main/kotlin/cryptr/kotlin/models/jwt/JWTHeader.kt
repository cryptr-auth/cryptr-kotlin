package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("header")
data class JWTHeader(
    @SerialName("kid") val kid: String,
    @SerialName("iss") val iss: String,
    @SerialName("typ") val typ: String,
    @SerialName("alg") val alg: String
) {

    init {
        require(kid.isNotEmpty() && kid.isNotBlank()) { "kid not compliant" }
        require(iss.isNotEmpty() && iss.isNotBlank()) { "iss not compliant" }
        require(typ == "JWT") { "require JWT typ" }
        require(alg == "RS256") { "only RS256 algo supported" }
    }

}
