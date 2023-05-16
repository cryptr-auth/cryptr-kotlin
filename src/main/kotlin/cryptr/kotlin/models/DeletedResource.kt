package cryptr.kotlin.models

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class DeletedResource {
    @Required
    @SerialName("deleted")
    abstract val deleted: Boolean

    @Required
    abstract val resource: CryptrResource
}
