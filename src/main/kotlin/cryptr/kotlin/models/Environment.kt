package cryptr.kotlin.models

import cryptr.kotlin.enums.EnvironmentStatus
import kotlinx.serialization.Serializable

/**
 * Represents [Organization] environment record
 */
@Serializable
data class Environment(
    /**
     * Name of the environment. Ex: `production` `sandbox`
     */
    val name: String,
    /**
     * Status of the enviroment. Ex: `UP` or `DOWN`
     *
     * @see EnvironmentStatus
     */
    val status: EnvironmentStatus
)
