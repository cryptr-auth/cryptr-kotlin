package cryptr.kotlin.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OrganizationStatusType {
    /**
     * [Organization] is still in creation
     */
    @SerialName("pending")
    PENDING,

    /**
     * [Organization] is ready to use
     */
    @SerialName("terminated")
    TERMINATED,
}