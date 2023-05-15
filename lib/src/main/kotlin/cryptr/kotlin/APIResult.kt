package cryptr.kotlin

import cryptr.kotlin.models.CryptrResource
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
sealed class APIResult<CryptrResource, ErrorMessage>


@Serializable
data class APISuccess<CryptrResource, ErrorMessage>(@Polymorphic val value: CryptrResource) :
    APIResult<CryptrResource, ErrorMessage>()

@Serializable
data class APIError<CryptrResource, ErrorMessage>(@Polymorphic val error: ErrorMessage) :
    APIResult<CryptrResource, ErrorMessage>()

@Serializable
data class ErrorMessage(val message: String)


