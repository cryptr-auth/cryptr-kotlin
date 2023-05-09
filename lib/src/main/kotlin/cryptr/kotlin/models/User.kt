package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    override val cryptrType: String = "User",
    val email: String,
    val id: String? = null,
    val address: Address? = null,
//    val metadata: Set<> = [],
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

    fun creationMap(): Map<String, String?> {
        return mapOf(
            "email" to email
        )
    }
}
