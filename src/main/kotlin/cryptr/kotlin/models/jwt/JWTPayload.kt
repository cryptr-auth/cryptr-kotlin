package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class JWTPayload(
    @SerialName("scp") val scp: Set<String>,
    @SerialName("aud") val aud: String?,
    @SerialName("email") val email: String?,
    @SerialName("ips") val ips: String?,
    @SerialName("sci") val sci: String?,
    @SerialName("sub") val sub: String,
    @SerialName("ver") val ver: Int,
    @SerialName("dbs") val dbs: String,
    @SerialName("iss") val iss: String,
    @SerialName("jtt") val jtt: String,
    @SerialName("tnt") val tnt: String,
    @SerialName("exp") val exp: Long,
    @SerialName("iat") val iat: Long,
    @SerialName("jti") val jti: String,
    @SerialName("cid") val cid: String,
    @SerialName("application_metadata") val applicationMetadata: Map<String, String>? = mapOf(),
    @SerialName("resource_owner_metadata") val resourceOwnerMetadata: Map<String, String>? = mapOf(),
) {

    init {
        require(ver == 1) { "only version '1' allowed" }
        require(scp.isNotEmpty()) { "scp cannot be empty" }
        require(sub.isNotEmpty() && sub.isNotBlank()) { "sub cannot be empty" }
        require(dbs.isNotEmpty() && dbs.isNotBlank()) { "dbs cannot be empty" }
        require(iss.isNotEmpty() && iss.isNotBlank()) { "iss cannot be empty" }
        require(jtt.isNotEmpty() && jtt.isNotBlank()) { "jtt cannot be empty" }
        require(jti.isNotEmpty() && jti.isNotBlank()) { "jti cannot be empty" }
        require(cid.isNotEmpty() && cid.isNotBlank()) { "cid cannot be empty" }
        require(exp > 0 && Instant.ofEpochSecond(exp).isAfter(Instant.now())) { "exp should be in the future" }
        require(iat > 0 && Instant.ofEpochSecond(iat).isBefore(Instant.now())) { "iat should be in the past" }
    }
}
