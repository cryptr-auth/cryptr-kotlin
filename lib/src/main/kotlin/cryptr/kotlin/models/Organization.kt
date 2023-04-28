package cryptr.kotlin.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject

data class Organization(
    val id: String? = null,
    val domain: String? = null,
    val name: String,
    val updatedAt: String? = null,
    val countryName: String = "FR",
    val state: String = "Nord",
    val insertedAt: String? = null,
    val locality: String? = "Lille"
) {
    companion object {
        const val apiResourceName: String = "organizations"
    }

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("id"),
        jsonObject.getString("domain"),
        jsonObject.getString("name"),
        jsonObject.getString("updated_at"),
        jsonObject.getString("country_name"),
        jsonObject.getString("state"),
        jsonObject.getString("inserted_at"),
        jsonObject.getString("locality"),
    )

    fun toJSONObject(): JsonObject {
        return buildJsonObject {
            put("name", name)
            if (domain !== null) put("domain", domain)
            put("locality", locality)
        }
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
