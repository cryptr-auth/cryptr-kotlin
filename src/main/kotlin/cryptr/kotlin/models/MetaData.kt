package cryptr.kotlin.models

import kotlinx.serialization.Serializable

/**
 * Represent meta attributes link to a [User]
 */
@Serializable
data class MetaData(
    /**
     * Unique identifier of the Metadata
     */
    val id: String? = null,
    /**
     * Key reference of the MetaData
     */
    val key: MetaKey,
    /**
     * [String] value of the MetaData
     */
    val value: String
)
