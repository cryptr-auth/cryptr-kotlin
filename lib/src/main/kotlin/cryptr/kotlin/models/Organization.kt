package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    val domain: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val name: String,
    @SerialName("inserted_at") val insertedAt: String? = null,
    val environments: Set<Environment>? = null
) {
    companion object {
        const val apiResourceName: String = "organizations"
    }

    fun creationMap(): Map<String, String?> {
        return mapOf(
            "name" to name
        )
    }
}
