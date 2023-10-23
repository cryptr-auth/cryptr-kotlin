package cryptr.kotlin.models;

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class MagicLinkChallenge(
    @SerialName("__type__") override val cryptrType: String = "MagicLinkChallenge",
    @SerialName("find_or_create") val findOrCreate: JsonElement? = null,
    @SerialName("request_id") val requestId: String,
    @SerialName("code") val code: String? = null,
    @SerialName("email_address") val emailAddress: String? = null,
    @SerialName("magic_link") val magicLink: String,
) : CryptrResource() {

}
