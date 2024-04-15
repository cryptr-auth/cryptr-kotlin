package cryptr.kotlin.models.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import java.time.Instant

/**
 * Representation of [JWTToken] payload claims
 */
@Serializable
data class JWTPayload(
    /**
     * Represents scopes allowed while using this JWT
     */
    @JsonNames("scp") val scope: JsonElement? = null,
    /**
     * Represents the audience where the token can be used from
     */
    @SerialName("aud") val aud: JsonElement? = null,
    /**
     * Represents the resource owner email to whom this token is attached to
     */
    @SerialName("email") val email: String? = null,
    /** (When generated from SSO) The SSO provider (ex: `okta`) */
    @SerialName("ips") val ips: String? = null,
    /** (When generated from SSO) The SSO Connection ID (ex: `nasa_1234`) */
    @SerialName("sci") val sci: String? = null,
    /**
     * Represents the resource owner unique ID to whom this token is attached to
     */
    @SerialName("sub") val sub: String,
    /**
     * Version of the current token.
     * SHOULD be superior or equals to `1`
     */
    @SerialName("ver") val ver: Int,
    /**
     * The resource owner database
     */
    val dbs: String? = null,
    /**
     * The issuer that generated the token
     */
    @SerialName("iss") val iss: String? = null,
    /**
     * The Type of token. Should be `JWT`
     */
    @SerialName("jtt") val jtt: String,
    /**
     * The nonce of token. Should be an UUID
     */
    @SerialName("nonce") val nonce: String? = null,
    /**
     * The Organization that owns the resource owner
     */
    @SerialName("tnt") val tnt: String? = null,
    /**
     * The Expiration date of the token
     */
    @SerialName("exp") val exp: Long,
    /**
     * The generation date
     */
    @SerialName("iat") val iat: Long,
    /**
     * The token unique identifier
     */
    @SerialName("jti") val jti: String,
    /**
     * The client id of the application responsible for the issuance of this token
     */
    @SerialName("cid") val cid: String? = null,
    /**
     * The Resource owner family name
     */
    @SerialName("family_name") val familyName: String? = null,
    /**
     * The Resource owner given name
     */
    @SerialName("given_name") val givenName: String? = null,
    /**
     * The metadata associated to the application
     */
    @SerialName("application_metadata") val applicationMetadata: Map<String, JsonElement>? = null,
    /**
     * The metadata associated to the resource owner
     */
    @JsonNames("meta_data", "resource_owner_metadata")
    @SerialName("meta_data")
    val metaData: Map<String, JsonElement>? = null,

    //V2
    /**
     * Current environment (v2)
     */
    @SerialName("env") val env: String? = null,
    /**
     * Current organization (v2)
     */
    @SerialName("org") val org: String? = null,
    /**
     * Current profile (openid v2)
     */
    @SerialName("profile") val profile: Map<String, JsonElement>? = null,
    /** (Openid) Authorized party */
    @SerialName("azp") val azp: String? = null,
    /**
     * The client id of the application responsible for the issuance of this token
     */
    @SerialName("client_id") val clientId: String? = null,
    //val scopes: JsonElement? = scp ?: scope,
    /** (When `openid`) For validating Access token */
    val at_hash: String? = null,
    /** (When `openid`) For validatio */
    val c_hash: String? = null,
    /** Is the email has been verified */
    val email_verified: JsonElement? = null,
    /** Is the phone number has been verified */
    val phone_number_verified: JsonElement? = null,
) {

    /**
     * domain of the organization handle the resource_owner
     */
    val domain: String
        get() = (org ?: tnt)!!

    /**
     * Current resource owner ID of the token
     */
    val subjectId: String
        get() = sub.split("|").last()

    init {
        require((1..3).contains(ver)) { "only versions 1 .. 3 are allowed" }
        //basic validations
        require(sub.isNotEmpty() && sub.isNotBlank()) { "sub cannot be empty" }
        require(jtt.isNotEmpty() && jtt.isNotBlank()) { "jtt cannot be empty" }
        require(jti.isNotEmpty() && jti.isNotBlank()) { "jti cannot be empty" }
        require(exp > 0 && Instant.ofEpochSecond(exp).isAfter(Instant.now())) { "exp should be in the future" }
        require(iat > 0 && Instant.ofEpochSecond(iat).isBefore(Instant.now())) { "iat should be in the past" }

        // ver is only 1 or 2
        if (ver == 1) {
            require(dbs!!.isNotEmpty() && dbs.isNotBlank()) { "dbs cannot be empty" }
            require(iss!!.isNotEmpty() && iss.isNotBlank()) { "iss cannot be empty" }
            require(cid!!.isNotEmpty() && cid.isNotBlank()) { "cid cannot be empty" }
        } else {
            // v2
            require(org!!.isNotEmpty() && org.isNotBlank()) { "org cannot be empty" }
            require(env!!.isNotEmpty() && env.isNotBlank()) { "env cannot be empty" }
        }
    }
}
