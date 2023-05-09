package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaData(
    @SerialName("__type__") override val cryptrType: String = "MetaData",
    @SerialName("__environment__") override val environment: String?,
    @SerialName("__domain__") override val resourceDomain: String?,
    val key: String,
    val value: String
) : CryptrResource()
