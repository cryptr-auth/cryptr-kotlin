package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents [User]'s address
 */
@Serializable
data class Address(
    /**
     * Cryptr Resource type . SHOULD be "Address"
     */
    @SerialName("__type__") override val cryptrType: String = "Address",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,
    /**
     * Country of the Address
     */
    val country: String? = null,
    /**
     * [String] representation of the address
     */
    val formatted: String? = null,
    /**
     * The city of the address
     */
    val locality: String? = null,
    /**
     * Zip code of the address
     */
    @SerialName("postal_code") val postalCode: String? = null,
    /**
     * Region/State of the address
     */
    val region: String? = null,
    /**
     * Street address
     */
    @SerialName("street_address") val streetAddress: String? = null
) : CryptrResource()
