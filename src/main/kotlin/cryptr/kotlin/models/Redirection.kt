package cryptr.kotlin.models

import cryptr.kotlin.models.connections.SSOConnection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Redirection configuration after successful authentication of a [SSOConnection]
 */
@Serializable
@SerialName("Redirection")
data class Redirection(
    @SerialName("__type__") override val cryptrType: String = "Redirection",
    @SerialName("__environment__") override val environment: String? = null,
    /**
     * Unique identifier of the Redirection
     */
    @SerialName("id") val id: String? = null,
    /**
     * URI where to redirect after login
     */
    @SerialName("url") val url: String,

    val as_default_for_environment: Environment? = null

) : CryptrResource()
