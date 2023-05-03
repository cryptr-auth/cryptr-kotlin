package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val email: String,
    val address: Address? = null,
    val birthdate: String? = null,
    val gender: String? = null,
    val profile: String? = null,
    @SerialName("given_name") val givenName: String? = null,
    val locale: String? = null,
    val picture: String? = null,
    val name: String? = null,
    val nickname: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("family_name") val familyName: String? = null,
    val website: String? = null,
    val zoneinfo: String? = null
) {
    companion object {
        const val apiResouceName: String = "profile"
    }
}
