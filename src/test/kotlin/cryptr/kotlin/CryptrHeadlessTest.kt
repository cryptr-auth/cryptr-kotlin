package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.enums.ChallengeType
import cryptr.kotlin.models.ChallengeResponse
import cryptr.kotlin.models.SSOChallenge
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@WireMockTest(proxyMode = true)
class CryptrHeadlessTest {

    private lateinit var cryptr: Cryptr

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        val defaultRedirectUri = "http://dev.cryptr.eu:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptr =
            Cryptr(tenantDomain, baseUrl, defaultRedirectUri, apiKeyClientId, apiKeyClientSecret)
        System.setProperty("CRYPTR_JWT_ALG", "HS256")
        System.setProperty(
            "CRYPTR_API_KEY_TOKEN",
            "eyJ0eXAiOiJKV1QiLCJpc3MiOiJodHRwOi8vZGV2LmNyeXB0ci5ldS90L3NoYXJrLWFjYWRlbXkiLCJraWQiOiIxMjM0NTY3ODc5IiwiYWxnIjoiSFMyNTYifQ.eyJjaWQiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJkYnMiOiJzYW5kYm94IiwiZXhwIjoxOTg0MzEwNjQzLCJpYXQiOjE2ODQzMDcwNDMsImlzcyI6Imh0dHA6Ly9kZXYuY3J5cHRyLmV1L3Qvc2hhcmstYWNhZGVteSIsImp0aSI6ImFhMTM3NDI5LTE1NDgtNDRmMC04ZTY4LTk3ZDAzYzFkMDBmNyIsImp0dCI6ImFwaV9rZXkiLCJzY3AiOiJyZWFkX21hbnk6c3NvX2Nvbm5lY3Rpb25zIHVwZGF0ZTpzc29fY29ubmVjdGlvbnMiLCJzdWIiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJ0bnQiOiJzaGFyay1hY2FkZW15IiwidmVyIjoxfQ.q20l-u-8gjsHDkW1IQUErVdgGykWrZmiGaojMMfrVD4"
        )
    }

    @Test
    fun createSSOChallengeForauth() {
        stubFor(
            post("/api/v2/sso-oauth-challenges")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"api_key_id\": \"some-api-key-id\",\n" +
                                "    \"authorization_url\": \"https://dev.cryptr.eu/org/acme-company/oauth2/saml?request_id=request-id\",\n" +
                                "    \"database\": \"sandbox\",\n" +
                                "    \"expired_at\": 1682672619,\n" +
                                "    \"redirect_uri\": \"http://dev.cryptr.eu:8080/callback\",\n" +
                                "    \"request_id\": \"request-id\",\n" +
                                "    \"saml_idp_id\": \"acme_company_BS8RohkywSxjoDnE2SEygL\"\n" +
                                "}"
                    )
                )
        )

        val challengeResponse =
            cryptr.createSsoChallenge(orgDomain = "acme-company", authType = ChallengeType.OAUTH)
        assertNotNull(challengeResponse)
        if (challengeResponse is APISuccess) {
            val challenge = challengeResponse.value
            assertEquals("request-id", challenge.requestId)
            assertEquals("sandbox", challenge.database)
            assertEquals("http://dev.cryptr.eu:8080/callback", challenge.redirectUri)
            assertEquals("acme_company_BS8RohkywSxjoDnE2SEygL", challenge.samlIdpId)
        }

    }

    @Test
    fun createSSOOauthChallengeForauth() {
        stubFor(
            post("/api/v2/sso-oauth-challenges")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"api_key_id\": \"some-api-key-id\",\n" +
                                "    \"authorization_url\": \"https://dev.cryptr.eu/org/acme-company/oauth2/saml?request_id=request-id\",\n" +
                                "    \"database\": \"sandbox\",\n" +
                                "    \"expired_at\": 1682672619,\n" +
                                "    \"redirect_uri\": \"http://dev.cryptr.eu:8080/callback\",\n" +
                                "    \"request_id\": \"request-id\",\n" +
                                "    \"saml_idp_id\": \"acme_company_BS8RohkywSxjoDnE2SEygL\"\n" +
                                "}"
                    )
                )
        )

        val challengeResponse = cryptr.createSsoOauthChallenge(orgDomain = "acme-company")
        assertNotNull(challengeResponse)
        if (challengeResponse is APISuccess) {
            val challenge = challengeResponse.value
            assertIs<SSOChallenge>(challenge)
            assertEquals("request-id", challenge.requestId)
        }
    }

    @Test
    fun createSSOSamlChallengeShouldReturnAuthorizationUrlIfRightOrga() {
        stubFor(
            post("/api/v2/sso-saml-challenges")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"api_key_id\": \"62847327-2101-4a36-a51c-e7016098ee18\",\n" +
                                "    \"authorization_url\": \"https://dev.cryptr.eu/org/acme-company/oauth2/saml?request_id=1546bfcf-9849-448c-a56a-265d1ef9c30d\",\n" +
                                "    \"database\": \"sandbox\",\n" +
                                "    \"expired_at\": 1682672619,\n" +
                                "    \"redirect_uri\": \"http://dev.cryptr.eu:8080/callback\",\n" +
                                "    \"request_id\": \"1546bfcf-9849-448c-a56a-265d1ef9c30d\",\n" +
                                "    \"saml_idp_id\": \"acme_company_BS8RohkywSxjoDnE2SEygL\"\n" +
                                "}"
                    )
                )
        )
        val challengeResponse = cryptr.createSsoSamlChallenge(orgDomain = "acme-company")
        assertNotNull(challengeResponse, "should return object")
        if (challengeResponse is APISuccess) {
            val challenge = challengeResponse.value
            assertNotNull(challenge.authorizationUrl)
            assertNotNull(challenge.requestId)
            assertTrue(URL(challenge.authorizationUrl).query.endsWith("1546bfcf-9849-448c-a56a-265d1ef9c30d"))
            assertIs<SSOChallenge>(challenge)
        }
    }

    @Test
    fun createSSOSamlChallengeShouldReturnAuthorizationUrlIfRightEmail() {
        stubFor(
            post("/api/v2/sso-saml-challenges")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"api_key_id\": \"62847327-2101-4a36-a51c-e7016098ee18\",\n" +
                                "    \"authorization_url\": \"https://dev.cryptr.eu/org/blablabus/oauth2/saml?request_id=5b2c5427-d5c3-4057-9cdc-b8d9914d2a7e\",\n" +
                                "    \"database\": \"sandbox\",\n" +
                                "    \"expired_at\": 1682673845,\n" +
                                "    \"redirect_uri\": \"http://localhost:8080/callback\",\n" +
                                "    \"request_id\": \"5b2c5427-d5c3-4057-9cdc-b8d9914d2a7e\",\n" +
                                "    \"saml_idp_id\": \"blablabus_JdZyJveBnj5kP6QstzF8LQ\"\n" +
                                "}"
                    )
                )
        )
        val challengeResponse = cryptr.createSsoSamlChallenge(userEmail = "john@blablabus.fr")
        if (challengeResponse is APISuccess) {
            val challenge = challengeResponse.value
            assertNotNull(challenge, "should return object")
            assertNotNull(challenge.authorizationUrl)
            assertNotNull(challenge.requestId)
            assertTrue(URL(challenge.authorizationUrl).query.endsWith("b2c5427-d5c3-4057-9cdc-b8d9914d2a7e"))
        }
    }

    @Test
    fun createSSOChallengeThrowsIfNoOrgOrEmail() {
        val e: Exception = assertThrows {
            cryptr.createSsoChallenge()
        }

        assertEquals("requires either orgDomain or endUser value", e.message)
    }


    @Test
    fun createSSOSamlChallengeShouldFailIfUnmatchingInput() {
        stubFor(
            post("/api/v2/sso-saml-challenges")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok("Not Found")
                )
        )
        val challengeResponse = cryptr.createSsoSamlChallenge(orgDomain = "azerty")
        if (challengeResponse is APIError) {
            val error = challengeResponse.error
            assertEquals("{\"error\":\"Not Found\"}", error.error.message)
        }
    }

    @Test
    fun consumeSSOSamlChallengeCallback() {
        stubFor(
            post("/oauth/token")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\"code\": \"sso-challenge-auth-code\"}"
                    )
                )
        )
        val resp = cryptr.validateSsoChallenge("some-code")
        assertIs<APISuccess<ChallengeResponse, ErrorMessage>>(resp)
        if (resp is APISuccess) {
            val value = resp.value
            //assertNotNull(value.clientUrl)
            //assertEquals("sso-challenge-auth-code", resp.getString("code"))
        }
    }

    @Test
    fun consumeSSOSamlChallengeCallbackThrowsWithoutProperCode() {
        val response1 = cryptr.validateSsoChallenge()
        assertIs<APIError<*, ErrorMessage>>(response1)
        if (response1 is APIError) {
            assertEquals("code is required", response1.error.error.message)
        }

        val response2 = cryptr.validateSsoChallenge("")
        assertIs<APIError<*, ErrorMessage>>(response2)
        if (response2 is APIError) {
            assertEquals("code is required", response2.error.error.message)
        }

    }
}