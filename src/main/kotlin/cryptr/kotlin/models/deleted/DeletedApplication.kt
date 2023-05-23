package cryptr.kotlin.models.deleted

import cryptr.kotlin.models.Application
import cryptr.kotlin.models.DeletedResource
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a deleted Application
 */
@Serializable
@SerialName("DeletedApplication")
data class DeletedApplication(
    /**
     * @see DeletedResource.deleted
     */
    @Required
    @SerialName("deleted")
    override val deleted: Boolean,

    /**
     * The deleted [Application]
     *
     * @see DeletedResource.resource
     */
    @Required
    @SerialName("resource")
    override val resource: Application
) : DeletedResource()
