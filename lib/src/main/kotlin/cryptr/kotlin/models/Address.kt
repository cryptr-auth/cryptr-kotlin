package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    override val cryptrType: String = "Address",
    val country: String? = null,
    val formatted: String? = null,
    val locality: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    val region: String? = null,
    @SerialName("street_address") val streetAddress: String? = null
) : CryptrResource()
