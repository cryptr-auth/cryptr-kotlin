package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class CryptrResource {
    @SerialName("__type__")
    val cryptrType: String = ""

    @SerialName("__environment__")
    val environment: String? = null

    @SerialName("__domain__")
    val resourceDomain: String? = null
}