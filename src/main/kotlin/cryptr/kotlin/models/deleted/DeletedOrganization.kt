package cryptr.kotlin.models.deleted

import cryptr.kotlin.models.DeletedResource
import cryptr.kotlin.models.Organization
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a deleted [Organization]
 */
@Serializable
@SerialName("DeletedOrganization")
data class DeletedOrganization(
    /**
     * @see DeletedResource.deleted
     */
    @Required
    @SerialName("deleted")
    override val deleted: Boolean,

    /**
     * Represents the deleted [Organization]
     *
     * @see DeletedResource.resource
     */
    @Required
    @SerialName("resource")
    override val resource: Organization
) : DeletedResource()
