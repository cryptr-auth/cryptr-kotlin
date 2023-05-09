package cryptr.kotlin.models

import cryptr.kotlin.CryptrSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = CryptrSerializer::class)
abstract class CryptrResource {
    @SerialName("__type__")
    abstract val cryptrType: String

    @SerialName("__environment__")
    open val environment: String? = null

    @SerialName("__domain__")
    open val resourceDomain: String? = null
}