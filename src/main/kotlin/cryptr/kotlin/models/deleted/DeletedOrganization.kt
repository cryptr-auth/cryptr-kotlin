package cryptr.kotlin.models.deleted

import cryptr.kotlin.models.DeletedResource
import cryptr.kotlin.models.Organization
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DeletedOrganization")
data class DeletedOrganization(
    @Required
    @SerialName("deleted")
    override val deleted: Boolean,
    @Required
    @SerialName("resource")
    override val resource: Organization
) : DeletedResource()
