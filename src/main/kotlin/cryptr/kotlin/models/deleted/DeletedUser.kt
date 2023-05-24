package cryptr.kotlin.models.deleted

import cryptr.kotlin.models.DeletedResource
import cryptr.kotlin.models.User
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a deleted [User]
 */
@Serializable
@SerialName("DeletedUser")
data class DeletedUser(
    /**
     * @see DeletedResource.deleted
     */
    @Required
    @SerialName("deleted")
    override val deleted: Boolean,

    /**
     * Represent the deleted [User]
     * @see DeletedResource.resource
     */
    @Required
    @SerialName("resource")
    override val resource: User
) : DeletedResource()
