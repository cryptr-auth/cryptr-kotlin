package cryptr.kotlin.models.deleted

import cryptr.kotlin.models.Application
import cryptr.kotlin.models.DeletedResource
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DeletedApplication")
data class DeletedApplication(
    @Required
    @SerialName("deleted")
    override val deleted: Boolean,
    @Required
    @SerialName("resource")
    override val resource: Application
) : DeletedResource()
