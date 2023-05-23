package cryptr.kotlin

import cryptr.kotlin.models.CryptrResource
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

/**
 * The Result of an API call Either:
 * - [APISuccess] representing the success of the [CryptrResource]
 * - [APIError] when something went wrong
 *
 */
@Serializable
sealed class APIResult<CryptrResource, ErrorMessage>


/**
 * Represents the Success of an API call
 *
 * @property value The [CryptrResource] of this success
 */
@Serializable
data class APISuccess<CryptrResource, ErrorMessage>(@Polymorphic val value: CryptrResource) :
    APIResult<CryptrResource, ErrorMessage>()

/**
 * Represents the failure of an API call
 *
 * @property error The [ErrorMessage] of this failure
 */
@Serializable
data class APIError<CryptrResource, ErrorMessage>(@Polymorphic val error: ErrorMessage) :
    APIResult<CryptrResource, ErrorMessage>()


/**
 * Represents a Error message
 *
 * @property message The [String] explanation of the error
 */
@Serializable
data class ErrorMessage(val message: String)


