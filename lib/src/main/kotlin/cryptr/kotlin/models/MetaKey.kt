package cryptr.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class MetaKey(
    val name: String,
    val id: String,
    val type: String,
    val required: Boolean
)
