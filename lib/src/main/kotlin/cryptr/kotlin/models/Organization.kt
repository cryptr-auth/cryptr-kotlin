package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    @SerialName("__type__") override val cryptrType: String = "Organization",
    val domain: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val name: String,
    @SerialName("inserted_at") val insertedAt: String? = null,
    val environments: Set<Environment> = setOf()
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "organizations"
    }

    fun creationMap(): Map<String, String?> {
        return mapOf(
            "name" to name
        )
    }
}
