package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.models.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@WireMockTest(proxyMode = true)
class CryptrTest {
    lateinit var cryptr: Cryptr

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        val defaultRedirectUri = "http://localhost:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptr = Cryptr(tenantDomain, baseUrl, defaultRedirectUri, apiKeyClientId, apiKeyClientSecret)
    }


    @Test
    fun retrieveApiKeyTokenWithStoreTokenShouldReturnsIt() {
        System.setProperty("CRYPTR_JWT_ALG", "HS256")
        val testToken =
            "eyJ0eXAiOiJKV1QiLCJpc3MiOiJodHRwOi8vZGV2LmNyeXB0ci5ldS90L3NoYXJrLWFjYWRlbXkiLCJraWQiOiIxMjM0NTY3ODc5IiwiYWxnIjoiSFMyNTYifQ.eyJjaWQiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJkYnMiOiJzYW5kYm94IiwiZXhwIjoxOTg0MzEwNjQzLCJpYXQiOjE2ODQzMDcwNDMsImlzcyI6Imh0dHA6Ly9kZXYuY3J5cHRyLmV1L3Qvc2hhcmstYWNhZGVteSIsImp0aSI6ImFhMTM3NDI5LTE1NDgtNDRmMC04ZTY4LTk3ZDAzYzFkMDBmNyIsImp0dCI6ImFwaV9rZXkiLCJzY3AiOiJyZWFkX21hbnk6c3NvX2Nvbm5lY3Rpb25zIHVwZGF0ZTpzc29fY29ubmVjdGlvbnMiLCJzdWIiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJ0bnQiOiJzaGFyay1hY2FkZW15IiwidmVyIjoxfQ.q20l-u-8gjsHDkW1IQUErVdgGykWrZmiGaojMMfrVD4"
        System.setProperty("CRYPTR_API_KEY_TOKEN", testToken)

        assertEquals(testToken, cryptr.retrieveApiKeyToken())
    }

    @Test
    fun retrieveApiKeyTokenWithPreviousFetchedTokenShouldReturnsIt() {
        System.clearProperty("CRYPTR_API_KEY_TOKEN")
        System.setProperty("CRYPTR_JWT_ALG", "HS256")
        val previousToken =
            "eyJ0eXAiOiJKV1QiLCJpc3MiOiJodHRwOi8vZGV2LmNyeXB0ci5ldS90L3NoYXJrLWFjYWRlbXkiLCJraWQiOiIxMjM0NTY3ODc5IiwiYWxnIjoiSFMyNTYifQ.eyJjaWQiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJkYnMiOiJzYW5kYm94IiwiZXhwIjoxOTg0MzEwNjQzLCJpYXQiOjE2ODQzMDcwNDMsImlzcyI6Imh0dHA6Ly9kZXYuY3J5cHRyLmV1L3Qvc2hhcmstYWNhZGVteSIsImp0aSI6ImFhMTM3NDI5LTE1NDgtNDRmMC04ZTY4LTk3ZDAzYzFkMDBmNyIsImp0dCI6ImFwaV9rZXkiLCJzY3AiOiJyZWFkX21hbnk6c3NvX2Nvbm5lY3Rpb25zIHVwZGF0ZTpzc29fY29ubmVjdGlvbnMiLCJzdWIiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJ0bnQiOiJzaGFyay1hY2FkZW15IiwidmVyIjoxfQ.q20l-u-8gjsHDkW1IQUErVdgGykWrZmiGaojMMfrVD4"
        System.setProperty("CRYPTR_CURRENT_API_KEY_TOKEN", previousToken)

        assertEquals(previousToken, cryptr.retrieveApiKeyToken())
    }

    @Test
    fun retrieveApiKeyTokenWithoutSystemShouldRequestEndpoint() {
        System.clearProperty("CRYPTR_API_KEY_TOKEN")
        System.clearProperty("CRYPTR_CURRENT_API_TOKEN")
        System.setProperty("CRYPTR_JWT_ALG", "HS256")
        val testToken =
            "eyJ0eXAiOiJKV1QiLCJpc3MiOiJodHRwOi8vZGV2LmNyeXB0ci5ldS90L3NoYXJrLWFjYWRlbXkiLCJraWQiOiIxMjM0NTY3ODc5IiwiYWxnIjoiSFMyNTYifQ.eyJjaWQiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJkYnMiOiJzYW5kYm94IiwiZXhwIjoxOTg0MzEwNjQzLCJpYXQiOjE2ODQzMDcwNDMsImlzcyI6Imh0dHA6Ly9kZXYuY3J5cHRyLmV1L3Qvc2hhcmstYWNhZGVteSIsImp0aSI6ImFhMTM3NDI5LTE1NDgtNDRmMC04ZTY4LTk3ZDAzYzFkMDBmNyIsImp0dCI6ImFwaV9rZXkiLCJzY3AiOiJyZWFkX21hbnk6c3NvX2Nvbm5lY3Rpb25zIHVwZGF0ZTpzc29fY29ubmVjdGlvbnMiLCJzdWIiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJ0bnQiOiJzaGFyay1hY2FkZW15IiwidmVyIjoxfQ.q20l-u-8gjsHDkW1IQUErVdgGykWrZmiGaojMMfrVD4"
        stubFor(
            post("/api/v2/oauth/token")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(ok("{\"access_token\": \"$testToken\"}"))

        )
        assertEquals(
            testToken, cryptr.retrieveApiKeyToken()
        )
    }

    @Test
    fun mapToFormDataShouldProperlyEncodeUser() {
        val user = User(
            email = "john@doe.com",
            profile = Profile(
                givenName = "John",
                familyName = "Doe"
            ),
            address = Address(
                locality = "Lille"
            )
        )

        val formData = cryptr.mapToFormData(JSONObject(Json.encodeToString(user)).toMap())
        assertContains(formData, "email=john%40doe.com")
        assertContains(formData, "profile[given_name]=John")
        assertContains(formData, "profile[family_name]=Doe")
        assertContains(formData, "address[locality]=Lille")
    }

    @Test
    fun toJSONStringShouldSerializeSuccess() {
        val resource = Organization(domain = "my-domain", name = "my Domain")
        val apiSuccess = APISuccess<CryptrResource, ErrorMessage>(resource)
        val jsonString = cryptr.toJSONString(apiSuccess)
        assertEquals(
            "{\"__type__\":\"Organization\",\"domain\":\"my-domain\"," +
                    "\"updated_at\":null,\"name\":\"my Domain\",\"inserted_at\":null,\"environments\":[],\"allowed_email_domains\":[]}",
            jsonString
        )
    }

    @Test
    fun toJSONStringShouldSerializeError() {
        val error = ErrorMessage.build(message = "something went wrong")
        val apiError = APIError<CryptrResource, ErrorMessage>(error)
        val jsonString = cryptr.toJSONString(apiError)
        assertEquals(
            "{\"error\":{\"type\":\"unhandled_error\",\"message\":\"something went wrong\",\"docUrls\":[]}}",
            jsonString
        )
    }
}
