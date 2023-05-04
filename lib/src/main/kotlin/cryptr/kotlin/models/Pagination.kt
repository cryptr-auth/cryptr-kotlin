package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    @SerialName("current_page") val currentPage: Int = 1,
    @SerialName("current_pages") val currentPages: Set<Int> = setOf(),
    @SerialName("next_page") val nextPage: Int = 1,
    @SerialName("per_page") val perPage: Int = 1,
    @SerialName("prev_page") val prevPage: Int? = null,
    @SerialName("total_pages") val totalPages: Int = 1,
)
