package cryptr.kotlin.models

import org.json.JSONObject

data class Profile(
    val email: String,
    val address: Any? = null,
    val birthdate: String? = null,
    val gender: String? = null,
    val profile: Any? = null,
    val givenName: String? = null,
    val locale: String? = null,
    val picture: String? = null,
    val name: String? = null,
    val nickname: String? = null,
    val phoneNumber: Any? = null,
    val familyName: String? = null,
    val website: String? = null,
    val zoneinfo: String? = null
) {
    companion object {
        const val apiResouceName: String = "profile"
    }

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("email"),
        jsonObject.opt("address"),
        jsonObject.optString("birthdate"),
        jsonObject.optString("gender"),
        jsonObject.opt("profile"),
        jsonObject.optString("given_name"),
        jsonObject.optString("locale"),
        jsonObject.optString("picture"),
        jsonObject.optString("name"),
        jsonObject.optString("nickname"),
        jsonObject.opt("phone_number"),
        jsonObject.optString("family_name"),
        jsonObject.optString("website"),
        jsonObject.optString("zoneinfo")
    )
}
