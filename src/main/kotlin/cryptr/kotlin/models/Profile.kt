package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent the identity of the [User]
 */
@Serializable
data class Profile(
    @SerialName("__type__") override val cryptrType: String = "Profile",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
    /**
     * Date of birth of the User (as [String])
     */
    val birthdate: String? = null,
    /**
     * Family name of the User
     */
    @SerialName("family_name") val familyName: String? = null,
    /**
     * Gender of the User. Ex: `male`or `female`
     */
    val gender: String? = null,
    /**
     * Given name of the User
     */
    @SerialName("given_name") val givenName: String? = null,
    /**
     * Preferred locale of User
     */
    val locale: String? = null,
    /**
     * Nickname of the User
     */
    val nickname: String? = null,
    /**
     * Picture of the User (ex Avatar)
     */
    val picture: String? = null,
    /**
     * Preferred Username
     */
    @SerialName("preferred_username") val preferredUsername: String? = null,
    val website: String? = null,
    /**
     * Geographical region of the User. Ex: `"Europe/Paris"`
     */
    @SerialName("zoneinfo") val zoneInfo: String? = null
) : CryptrResource() {
    companion object {
        const val apiResouceName: String = "profile"
    }
}
