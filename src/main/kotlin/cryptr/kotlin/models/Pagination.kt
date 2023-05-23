package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent the current pagination for a [List]
 */
@Serializable
data class Pagination(
    /**
     * Current (chosen page). Cannot be negative
     */
    @SerialName("current_page") val currentPage: Int = 1,
    /**
     * Current surrounding pages if you would like to display the navigation.
     * Ex: [2,3,4,5,6]
     */
    @SerialName("current_pages") val currentPages: Set<Int> = setOf(),
    /**
     * Next available page.
     * If you are on your last page value will be `null`.
     * Ex: you have 10 records and you chose 5 per page value will be 2 if you are on the page `1`
     *
     */
    @SerialName("next_page") val nextPage: Int? = null,
    /**
     * How many items you want in your List pages
     */
    @SerialName("per_page") val perPage: Int = 1,
    /**
     * The previous available page. Can be null or positive
     */
    @SerialName("prev_page") val prevPage: Int? = null,
    /**
     * Total available pages depending on [perPage]  and [List.total]
     */
    @SerialName("total_pages") val totalPages: Int = 1,
)
