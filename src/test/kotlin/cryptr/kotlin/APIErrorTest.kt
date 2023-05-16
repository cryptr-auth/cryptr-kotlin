package cryptr.kotlin

import cryptr.kotlin.models.CryptrResource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class APIErrorTest {

    @Test
    fun getError() {
        val errorMessage = ErrorMessage("some error message")
        val apiError = APIError<CryptrResource, ErrorMessage>(error = errorMessage)
        assertEquals("some error message", apiError.error.message)
    }

    @Test
    @Ignore
    fun apiErrorSerialization() {
        val errorMessage = ErrorMessage("some error message")
        val apiError = APIError<CryptrResource, ErrorMessage>(error = errorMessage)
        val serialized = Json.encodeToString<APIError<CryptrResource, ErrorMessage>>(value = apiError)
        assertEquals("", serialized)
    }
}