package cryptr.kotlin

import cryptr.kotlin.models.CryptrResource
import kotlinx.serialization.Serializable

sealed class APIResult<CryptrResource, ErrorMessage>


data class APISuccess<CryptrResource, ErrorMessage>(val value: CryptrResource) :
    APIResult<CryptrResource, ErrorMessage>()

@Serializable
data class APIError<CryptrResource, ErrorMessage>(val value: ErrorMessage) : APIResult<CryptrResource, ErrorMessage>()

@Serializable
data class ErrorMessage(val message: String)


