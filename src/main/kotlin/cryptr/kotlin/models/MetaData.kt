package cryptr.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class MetaData(
    val id: String? = null,
    val key: MetaKey,
    val value: String
)
