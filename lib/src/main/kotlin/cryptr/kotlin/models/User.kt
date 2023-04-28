package cryptr.kotlin.models

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject

data class User(
    val profile: Profile,
    val id: String? = null,
    val updatedAt: String? = null,
    val insertedAt: String? = null
) {
    companion object {
        const val apiResourceName: String = "users"
    }

    constructor(jsonObject: JSONObject) : this(
        Profile(jsonObject.getJSONObject("profile")),
        jsonObject.getString("id"),
        jsonObject.getString("updated_at"),
        jsonObject.getString("inserted_at")
    )

    fun toJSonObject(): JsonObject {
        return buildJsonObject {
            put("id", id)
            put("email", profile.email)
            put("updated_at", updatedAt)
            put("inserted_at", insertedAt)
        }
    }

    fun creationMap(): Map<String, String?> {
        return mapOf(
            "email" to profile.email
        )
    }
}
