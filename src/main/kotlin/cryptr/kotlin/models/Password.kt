package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Password(
    @SerialName("__type__") override val cryptrType: String = "Password",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    @SerialName("code") val passwordCode: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("id") val id: Int? = null,

    ) : CryptrResource()