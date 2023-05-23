package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a result of a INDEX call request
 */
@Serializable
@SerialName("List")
data class List<T : CryptrResource>(
    @SerialName("__type__") override val cryptrType: String = "List",
    /**
     * List of records matching the request and (optional) pagination
     */
    @SerialName("data") val data: Set<T> = setOf(),
    /**
     * The pagination of the current list
     */
    @SerialName("pagination") val pagination: Pagination,
    /**
     * Total records matching the request regardless the current pagination
     */
    val total: Int = 0
) : CryptrResource()