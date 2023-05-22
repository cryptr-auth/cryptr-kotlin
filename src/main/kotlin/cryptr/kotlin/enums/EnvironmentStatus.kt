package cryptr.kotlin.enums

import cryptr.kotlin.models.Environment
import cryptr.kotlin.models.Organization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent statuses of Organization's environments
 *
 * @see Organization.environment
 * @see [Environment]
 */
@Serializable
enum class EnvironmentStatus {
    /**
     * The [Environment] is active and can be used
     */
    @SerialName("up")
    UP,

    /**
     * The [Environment] is disabled and related children cannot be managed
     */
    @SerialName("down")
    DOWN,
}