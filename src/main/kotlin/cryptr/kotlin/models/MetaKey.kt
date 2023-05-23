package cryptr.kotlin.models

import kotlinx.serialization.Serializable

/**
 * Key representation for a [MetaData]
 */
@Serializable
data class MetaKey(
    /**
     * Friendly name of the MetaData key
     */
    val name: String,
    /**
     * Unique identifier of this current key
     */
    val id: String,
    /**
     * Type of value that will be associated. ex: `number` , `date`...
     */
    val type: String,
    /**
     * Is the presence of a value for this Key is mandatory
     */
    val required: Boolean
)
