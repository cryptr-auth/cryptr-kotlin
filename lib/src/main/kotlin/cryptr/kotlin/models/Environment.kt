package cryptr.kotlin.models

import cryptr.kotlin.enums.EnvironmentStatus
import kotlinx.serialization.Serializable

@Serializable
data class Environment(
//    val id: String,
    val name: String,
    val status: EnvironmentStatus
)
