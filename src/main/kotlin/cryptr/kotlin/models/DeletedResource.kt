package cryptr.kotlin.models

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a deletion result of a resource
 */
@Serializable
abstract class DeletedResource {
    /**
     * If the resource has been or not deleted
     */
    @Required
    @SerialName("deleted")
    abstract val deleted: Boolean

    /**
     * The requested resource deleted
     */
    @Required
    abstract val resource: CryptrResource
}
