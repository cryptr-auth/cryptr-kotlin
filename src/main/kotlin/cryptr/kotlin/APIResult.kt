package cryptr.kotlin

import cryptr.kotlin.models.CryptrResource
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
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

@Serializable
data class ErrorContent(
    val type: String? = "unhandled_error",
    val message: String,
    @SerialName("doc_urls") val docUrls: Set<String>? = emptySet()
)

/**
 * Represents a Error message
 *
 * @property message The [String] explanation of the error
 */
@Serializable
data class ErrorMessage(val error: ErrorContent) {
    companion object {
        fun build(
            message: String,
            type: String? = "unhandled_error",
            docUrls: Set<String>? = emptySet()
        ): ErrorMessage {
            val errorContent = ErrorContent(type = type, message = message, docUrls = docUrls)
            return ErrorMessage(errorContent)
        }
    }
}


