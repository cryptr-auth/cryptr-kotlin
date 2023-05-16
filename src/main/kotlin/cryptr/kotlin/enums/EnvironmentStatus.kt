package cryptr.kotlin.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EnvironmentStatus {
    @SerialName("up")
    UP,
    @SerialName("down")
    DOWN,
}