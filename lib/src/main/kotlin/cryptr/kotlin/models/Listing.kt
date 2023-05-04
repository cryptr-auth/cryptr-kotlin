package cryptr.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class Listing<T : CryptrResource>(
    val data: Set<T> = setOf(),
    val pagination: Pagination,
    val total: Int = 0
) : CryptrResource()