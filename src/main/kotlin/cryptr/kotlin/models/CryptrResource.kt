package cryptr.kotlin.models

import cryptr.kotlin.CryptrSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Basic Structure params of a Cryptr Resouce
 */
@Serializable(with = CryptrSerializer::class)
abstract class CryptrResource {
    /**
     * type of the retrieved resource. Ex: User, SSOConnection ...
     */
    @SerialName("__type__")
    abstract val cryptrType: String

    /**
     * Database where the resource is stored (ex: `sandbox` or `production`)
     */
    @SerialName("__environment__")
    open val environment: String? = null

    /**
     * Domain that owns the resource
     */
    @SerialName("__domain__")
    open val resourceDomain: String? = null
}