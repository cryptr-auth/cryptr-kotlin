package cryptr.kotlin.models

import org.json.JSONObject

data class Address(
    val id: String? = null,
    val country: String? = null,
    val streetAddress: String? = null,
    val formatted: String? = null,
    val postalCode: String? = null,
    val region: String? = null
) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.optString("id"),
        jsonObject.optString("country"),
        jsonObject.optString("street_address"),
        jsonObject.optString("formatted"),
        jsonObject.optString("postal_code"),
        jsonObject.optString("region"),
    )

}
