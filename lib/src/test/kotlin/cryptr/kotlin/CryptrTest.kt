package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.enums.ApplicationType
import cryptr.kotlin.models.Address
import cryptr.kotlin.models.Application
import cryptr.kotlin.models.Profile
import cryptr.kotlin.models.User
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

    companion object {

    }

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
    fun retrievedApiKeyTokenWithStoreTokenShouldReturnsIt() {
        System.setProperty("CRYPTR_API_KEY_TOKEN", "stored-api-key")

        assertEquals("stored-api-key", cryptr.retrieveApiKeyToken())
    }

    @Test
    fun retrievedApiKeyTokenWithPreviousFetchedTokenShouldReturnsIt() {
        System.setProperty("CRYPTR_API_KEY_TOKEN", "previous-fetched-api-key")

        assertEquals("previous-fetched-api-key", cryptr.retrieveApiKeyToken())
    }

    @Test
    fun retrieveApiKeyTokenWithoutSystemShouldRequestEndpoint() {
        System.clearProperty("CRYPTR_API_KEY_TOKEN")
        stubFor(
            post("/api/v2/oauth/token")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(ok("{\"access_token\": \"some-jwt-token\"}"))

        )
        assertEquals("some-jwt-token", cryptr.retrieveApiKeyToken())
    }

    @Test
    fun getTenantDomain() {
    }

    @Test
    fun getBaseUrl() {
    }

    @Test
    fun getDefaultRedirectUrl() {
    }

    @Test
    fun getApiKeyClientId() {
    }

    @Test
    fun getApiKeyClientSecret() {
    }

    @Test
    fun getLogger() {
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
}
