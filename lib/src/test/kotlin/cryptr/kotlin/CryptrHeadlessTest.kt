package cryptr.kotlin

import cryptr.kotlin.models.SSOChallenge
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class CryptrHeadlessTest {

    companion object {
        val tenantDomain = "shark-academy"
        val baseUrl = "https://cleeck-umbrella-develop.onrender.com"
        val defaultRedirectUrl = "http://localhost:8080/callback"
        val apiKeyClientId = "62847327-2101-4a36-a51c-e7016098ee18"
        val apiKeyClientSecret = "qO0vCgXyUk7OjCZIswZ6Tmhjfu8Gqnz7v0bQLztCFsGMZ+nCzyBwdJtgibK8ST+X"
        val cryptrHeadless =
            CryptrHeadless(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret)
    }

    @Test
    fun createSSOSamlChallenge() {
        val challengeResponse = cryptrHeadless.createSSOSamlChallenge(orgDomain = "acme-company")
        assertNotNull(challengeResponse, "should return object")
        val challenge = SSOChallenge(challengeResponse)
        assertNotNull(challenge.authorizationUrl)
        assertNotNull(challenge.requestId)
    }

    @Test
    fun consumeSSOSamlChallengeCallback() {
        assertNull(cryptrHeadless.consumeSSOSamlChallengeCallback("some-code"))
    }
}