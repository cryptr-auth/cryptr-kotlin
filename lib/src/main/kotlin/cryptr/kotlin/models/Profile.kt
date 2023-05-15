package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("__type__") override val cryptrType: String = "Profile",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
    val birthdate: String? = null,
    @SerialName("family_name") val familyName: String? = null,
    val gender: String? = null,
    @SerialName("given_name") val givenName: String? = null,
    val locale: String? = null,
    val nickname: String? = null,
    val picture: String? = null,
    @SerialName("preferred_username") val preferredUsername: String? = null,
    val website: String? = null,
    @SerialName("zoneinfo") val zoneInfo: String? = null
) : CryptrResource() {
    companion object {
        const val apiResouceName: String = "profile"
    }
}
