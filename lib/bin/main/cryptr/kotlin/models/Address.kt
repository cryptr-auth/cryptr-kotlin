package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String? = null,
    val country: String? = null,
    @SerialName("street_address") val streetAddress: String? = null,
    val formatted: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    val region: String? = null
) {
}
