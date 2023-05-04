package cryptr.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
abstract class Listing(
    val data: Set<CryptrResource> = setOf(),
    val pagination: Pagination,
    val total: Int = 0
) : CryptrResource()