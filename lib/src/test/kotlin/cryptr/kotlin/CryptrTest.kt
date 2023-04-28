package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@WireMockTest(proxyMode = true)
class CryptrTest {
    var cryptr: Cryptr? = null

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
    fun makeRequest() {
    }

    @Test
    fun retrievedApiKeyTokenWithStoreTokenShouldReturnsIt() {
        System.setProperty("CRYPTR_API_KEY_TOKEN", "stored-api-key")

        assertEquals("stored-api-key", cryptr?.retrieveApiKeyToken())
    }

    @Test
    fun retrievedApiKeyTokenWithPreviousFetchedTokenShouldReturnsIt() {
        System.setProperty("CRYPTR_API_KEY_TOKEN", "previous-fetched-api-key")

        assertEquals("previous-fetched-api-key", cryptr?.retrieveApiKeyToken())
    }

    @Test
    fun retrieveApiKeyTokenWithoutSystemShouldRequestEndpoint() {
        stubFor(
            post("/api/v2/oauth/token")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(ok("{\"access_token\": \"some-jwt-token\"}"))

        )
        assertEquals("some-jwt-token", cryptr?.retrieveApiKeyToken())
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
}
