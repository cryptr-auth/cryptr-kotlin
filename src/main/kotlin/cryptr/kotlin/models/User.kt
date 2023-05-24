package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent a User stored in Cryptr Service
 */
@Serializable
data class User(
    @SerialName("__type__") override val cryptrType: String = "User",
    /**
     * In which [Organization]'s environment(database) the user is stored
     */
    @SerialName("__environment__") override val environment: String? = null,
    /**
     * Which [Organization] owns the User
     */
    @SerialName("__domain__") override val resourceDomain: String? = null,
    /**
     * Email of the user
     */
    val email: String,
    /**
     * Unique identifier of the User
     */
    val id: String? = null,
    /**
     * The postal Address of the User. Can be null
     */
    val address: Address? = null,
    /**
     * All custom meta attributes that can be attached to a User.
     * Example of purposes:
     * - Data sent though SSO SAML Authentication
     * - Custom Data required to identify the user in your services
     */
    @SerialName("meta_data") val metadata: Set<MetaData> = setOf(),
    /**
     * The phone number of the User
     */
    @SerialName("phone_number") val phoneNumber: String? = null,
    /**
     * Is the phone number has been verified
     */
    @SerialName("phone_number_verified") val phoneNumberVerified: Boolean = false,
    /**
     * Is the email has been verified
     */
    @SerialName("email_verified") val emailVerified: Boolean = false,
    /**
     * Identity of the User (family name ...)
     */
    val profile: Profile? = null,
    /**
     * Date of creation of the User
     */
    @SerialName("inserted_at") val insertedAt: String? = null,
    /**
     * Last date of update of the User
     */
    @SerialName("updated_at") val updatedAt: String? = null,
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "users"
    }
}
