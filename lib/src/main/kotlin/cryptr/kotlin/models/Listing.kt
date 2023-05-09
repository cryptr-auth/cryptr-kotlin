package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Listing<T : CryptrResource>(
    override val cryptrType: String = "List",
    val data: Set<T> = setOf(),
    @SerialName("paginate") val pagination: Pagination,
    val total: Int = 0
) : CryptrResource()