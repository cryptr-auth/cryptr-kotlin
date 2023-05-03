package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    val id: String? = null,
    val domain: String? = null,
    val name: String,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("country_name") val countryName: String = "FR",
    val state: String = "Nord",
    @SerialName("inserted_at") val insertedAt: String? = null,
    val locality: String? = "Lille"
) {
    companion object {
        const val apiResourceName: String = "organizations"
    }

    fun creationMap(): Map<String, String?> {
        return mapOf(
            "name" to name,
            "locality" to locality,
            "state" to state,
            "country_name" to countryName
        )
    }
}
