package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Identity(
    @SerialName("idp_id") val idpId: String,
    @SerialName("authenticated_at") val authenticatedAt: Long,
    val provider: String,
    val data: Map<String, JsonElement>? = null
) {
    init {
        require(provider.substringBefore(".") in listOf("saml", "oauth")) {
            "Prefix in '${provider}' must be in ['saml', 'oauth']"
        }
    }
}
