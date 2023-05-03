package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val profile: Profile,
    val id: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("inserted_at") val insertedAt: String? = null
) {
    companion object {
        const val apiResourceName: String = "users"
    }

    fun creationMap(): Map<String, String?> {
        return mapOf(
            "profile[email]" to profile.email
        )
    }
}
