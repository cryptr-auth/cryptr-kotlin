package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("__type__") override val cryptrType: String = "Address",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
    val country: String? = null,
    val formatted: String? = null,
    val locality: String? = null,
    @SerialName("postal_code") val postalCode: String? = null,
    val region: String? = null,
    @SerialName("street_address") val streetAddress: String? = null
) : CryptrResource()
