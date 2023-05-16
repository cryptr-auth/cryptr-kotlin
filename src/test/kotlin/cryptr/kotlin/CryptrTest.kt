package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.enums.ApplicationType
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
        val defaultRedirectUrl = "http://localhost:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptr = Cryptr(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret)
    }


    @Test
    fun retrieveApiKeyTokenWithStoreTokenShouldReturnsIt() {
        System.setProperty("CRYPTR_API_KEY_TOKEN", "stored-api-key")

        assertEquals("stored-api-key", cryptr.retrieveApiKeyToken())
    }

    @Test
    fun retrieveApiKeyTokenWithPreviousFetchedTokenShouldReturnsIt() {
        System.clearProperty("CRYPTR_API_KEY_TOKEN")
        System.setProperty("CRYPTR_CURRENT_API_TOKEN", "previous-fetched-api-key")

        assertEquals("previous-fetched-api-key", cryptr.retrieveApiKeyToken())
    }

    @Test
    fun retrieveApiKeyTokenWithoutSystemShouldRequestEndpoint() {
        System.clearProperty("CRYPTR_API_KEY_TOKEN")
        System.clearProperty("CRYPTR_CURRENT_API_TOKEN")
        stubFor(
            post("/api/v2/oauth/token")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(ok("{\"access_token\": \"some-jwt-token\"}"))

        )
        assertEquals("some-jwt-token", cryptr.retrieveApiKeyToken())
    }


    @Test
    fun mapToFormDataShouldProperlyEncodeApplication() {
        val application = Application(
            name = "My Application",
            applicationType = ApplicationType.REGULAR_WEB,
            allowedOriginsCors = setOf("https://www.example.com"),
            allowedRedirectUrls = setOf("https://www.example.com/callback"),
            allowedLogoutUrls = setOf("https://www.example.com/logout")
        )

        val formData = cryptr.mapToFormData(JSONObject(Json.encodeToString(application)).toMap())

        assertContains(formData, "application_type=regular_web")
        assertContains(formData, "name=My+Application")
        assertContains(formData, "allowed_origins_cors[]=https%3A%2F%2Fwww.example.com")
        assertContains(formData, "allowed_redirect_urls[]=https%3A%2F%2Fwww.example.com%2Fcallback")
        assertContains(formData, "allowed_logout_urls[]=https%3A%2F%2Fwww.example.com%2Flogout")
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
                    "\"updated_at\":null,\"name\":\"my Domain\",\"inserted_at\":null,\"environments\":[]}",
            jsonString
        )
    }

    @Test
    fun toJSONStringShouldSerializeError() {
        val error = ErrorMessage(message = "something went wrong")
        val apiError = APIError<CryptrResource, ErrorMessage>(error)
        val jsonString = cryptr.toJSONString(apiError)
        assertEquals("{\"message\":\"something went wrong\"}", jsonString)
    }
}