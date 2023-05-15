package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("__type__") override val cryptrType: String = "User",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
    val email: String,
    val id: String? = null,
    val address: Address? = null,
    @SerialName("meta_data") val metadata: Set<MetaData> = setOf(),
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("phone_number_verified") val phoneNumberVerified: Boolean = false,
    @SerialName("email_verified") val emailVerified: Boolean = false,
    val profile: Profile? = null,
    @SerialName("inserted_at") val insertedAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "users"
    }
}
