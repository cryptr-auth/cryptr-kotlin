package cryptr.kotlin.models.deleted

import cryptr.kotlin.models.DeletedResource
import cryptr.kotlin.models.User
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DeletedUser")
data class DeletedUser(
    @Required
    @SerialName("deleted")
    override val deleted: Boolean,
    @Required
    @SerialName("resource")
    override val resource: User
) : DeletedResource()
