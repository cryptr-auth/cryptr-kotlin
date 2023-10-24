package cryptr.kotlin.models

import cryptr.kotlin.models.jwt.JWTPayload
import cryptr.kotlin.models.jwt.JWTToken
import org.junit.jupiter.api.Test
import kotlin.test.*

class ChallengeResponseTest {

    private var serviceUrl = "https://cleeck-umbrella-develop.onrender.com"
    private var v2AccessToken =
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY2xlZWNrLXVtYnJlbGxhLWRldmVsb3Aub25yZW5kZXIuY29tL3QvY3JlYXRlZ3JhbSIsImtpZCI6IjljYWQ5ZjAyLWNkN2EtNGQ5OC05ZWFiLTcyNDA2MjE4ZjAwYyIsInR5cCI6IkpXVCJ9.eyJhcHBsaWNhdGlvbl9tZXRhZGF0YSI6e30sImF1ZCI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAvY2FsbGJhY2siXSwiYXpwIjoiYTUyOGMxYjEtZTM3Zi00MGIyLWJhZTYtNjJhNmFkOTUwZWZiIiwiY2lkIjoiYTUyOGMxYjEtZTM3Zi00MGIyLWJhZTYtNjJhNmFkOTUwZWZiIiwiY2xpZW50X2lkIjoiYTUyOGMxYjEtZTM3Zi00MGIyLWJhZTYtNjJhNmFkOTUwZWZiIiwiZW1haWwiOiJqYW5lLmRvZUBjcnlwdHIuY28iLCJlbnYiOiJzYW5kYm94IiwiZXhwIjoxNjk4MTc1NTE4LCJpYXQiOjE2OTgxMzk1MTgsImlwcyI6Im9rdGEiLCJqdGkiOiJjNjY1N2E0Yi0zZWE5LTQ4NmEtYTNiNS0wZjc0YTc1ZGY0ZTIiLCJqdHQiOiJhY2Nlc3MiLCJvcmciOiJjcmVhdGVncmFtIiwic2NpIjoiY3JlYXRlZ3JhbV85Z0pTVWVXV21ES0xWRlJWNVZ1YXZoIiwic2NvcGUiOlsib3BlbmlkIiwiZW1haWwiLCJwcm9maWxlIl0sInN1YiI6ImNyeXB0cnxiMjcyNzEyNy01Mjg1LTRlNzYtYThhZS00MWJkZDA2MmRlYWYiLCJ2ZXIiOjJ9.BpVvSLtHvCDl4O_k3zBJPrSFTysmLMCg8G0rRu-43U5hVkHFAHlAKYvosMRe5JqQS2-Z_KEfd6J3FgP0ju8yX_BfYwXAg60u89AVoV9n4DPlAgsLW1nBs5nDfqD-lM7JfEwZj7Ri7gBKzxljoGK6Di15ufEFbXbELyr1brbar9VCYCLEB2IL_-tupkRxytSYftXkRxzQ9Ep2wNWbxr05man9-51GoMJ6pJYhTzc5u4R7C5oNkVgy482SK04bWghb35VtCnfUsVm4CghgwFTr35D1CXgJOxXSfP_3seKnETHESUH6i_I5XM_Iy9XQJxRMQHVkbQSVzdILBRjtj16Sq3ZY7HENSnCojAkyEH0b7r8pnwjwnLkyyHUs9CBw2ZJVw2NvV4-KodLAOABsaGLYs25FgSbtxG1rhU_QkiNFO_5XJSgYgQBNz6APzuPxW_ff3CrHcVfrq5cpQ4UZeKpTHMHEUUyeZ4LwJ5oGLH8v0NqgzOyeIqmfoplT-btMc8RTKV6N6ZhOKTHLcPxjWWt_o49sEdEv486jNDNRtJxB9hxUBSXQ0l2L8nfM_wpmU-h6ngyveaMPyLSDvT1jkfyTYVmDwDFxFoy4xN1Eju7aH72Amur1FwjTn_D7xrNQCrNuwmZ3aOzBbQMEdIuJgN4BPJqSnUu0I6jZwFzsLpMJdyQ"
    private var v2IdToken =
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY2xlZWNrLXVtYnJlbGxhLWRldmVsb3Aub25yZW5kZXIuY29tL3QvY3JlYXRlZ3JhbSIsImtpZCI6IjljYWQ5ZjAyLWNkN2EtNGQ5OC05ZWFiLTcyNDA2MjE4ZjAwYyIsInR5cCI6IkpXVCJ9.eyJhcHBsaWNhdGlvbl9tZXRhZGF0YSI6e30sImF0X2hhc2giOiJWVTVwMUs5anFFdzFheEoxMnNBaW93IiwiYXVkIjoiYTUyOGMxYjEtZTM3Zi00MGIyLWJhZTYtNjJhNmFkOTUwZWZiIiwiY19oYXNoIjoiQi0yYUhjb1ZsemJDU1FrcnpsQW5mdyIsImVtYWlsIjoiamFuZS5kb2VAY3J5cHRyLmNvIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJlbnYiOiJzYW5kYm94IiwiZXhwIjoxNjk4MTc1NTE4LCJpYXQiOjE2OTgxMzk1MTgsImlwcyI6Im9rdGEiLCJqdGkiOiJhOGNjYzIxYy00YTBhLTRiMTgtOTUwZS0xZGFiYWNhMTAwODkiLCJqdHQiOiJvcGVuaWQiLCJtZXRhX2RhdGEiOnsiZW1haWwiOiJqYW5lLmRvZUBjcnlwdHIuY28iLCJmaXJzdF9uYW1lIjoiSmFuZSIsImxhc3RfbmFtZSI6IkRvZSIsIm9yZ19kb21haW4iOiJjcmVhdGVncmFtIiwic2FtbF9uYW1laWQiOiJqYW5lLmRvZUBjcnlwdHIuY28iLCJzYW1sX3N1YmplY3QiOiJqYW5lLmRvZUBjcnlwdHIuY28iLCJ1aWQiOiJqYW5lLmRvZUBjcnlwdHIuY28ifSwib3JnIjoiY3JlYXRlZ3JhbSIsInBob25lX251bWJlcl92ZXJpZmllZCI6ZmFsc2UsInByb2ZpbGUiOnsiZmFtaWx5X25hbWUiOiJEb2UiLCJnaXZlbl9uYW1lIjoiSmFuZSJ9LCJzY2kiOiJjcmVhdGVncmFtXzlnSlNVZVdXbURLTFZGUlY1VnVhdmgiLCJzdWIiOiJjcnlwdHJ8YjI3MjcxMjctNTI4NS00ZTc2LWE4YWUtNDFiZGQwNjJkZWFmIiwidmVyIjoyfQ.N1Jx7TwpkgC4kTyJe2JQHCT3lBCnxs0IkDMUTeI1qxYimBj9eEJkAKZmG5BR9MOATEY8RiclvC2h_OZ8iFxxMbYwdUt_q7nH7sxFxvyWdn1cHIySeXNHTZoo7_7qP7DEE4Sx9QQtNyAqnvRn3EKvGPAHHrCxzEjpyDrKyakRjR2yAMXRTWfQ4ywX2D2sWyidsgSvzTcregcYkIHqGgNwMaB_Rq9h8_APn-5Uw3jA17T9DK63anwJVXJ58TBdK9BegBFHyf1JUZLLa_QwFHmOvmN50a9foW_Ly_fwY2BVfzwuBkp6Uy2JK5Pw1xGLbjdseuBsOFM8RsCwenZ5pTkiEf0WP3wj54BeuFfuY5TcvrP0vsqHlfils_zSsyLihVA4wixN-q8asPK7yuoxNhEwdQvFxKJyLgnLj6mW5SbdkuWSjGhjX-Au3Fze1iV-sVC1BYT0oRYNN4C_3tGHNFTx6RlE9cRvtosK84U7LHpPDKsGbzmpDYya1M85qfq3OQ6WcUgV45eSQzFqd6GLtyp0dfbhbdben8QmOH4IkSN4i3hgm_fhnJxs5hFpFxvWxZeb1ivZ-W2YpZvV3EaZRVgCw_sM_ptdakCVvoaGnr6s-cOsjSdVTjViA_VAJa3YRJbGWwQg6VyWpmA4z7-hXa3QrNqlKl7Sja6N-bKUfn-alVI"

    @Test
    fun shouldFailIfEmptyChallengeResponse() {
        assertNull(ChallengeResponse().getIdToken(serviceUrl))
        assertNull(ChallengeResponse().getAccessToken(serviceUrl))
    }

    @Test
    fun shouldSucceedChallengeResponse() {
        val challengeResponse = ChallengeResponse(accessToken = v2AccessToken, idToken = v2IdToken)
        val idToken = challengeResponse.getIdToken(serviceUrl)
        val accessToken = challengeResponse.getAccessToken(serviceUrl)
        assertNotNull(idToken)
        assertNotNull(accessToken)
        assertTrue(idToken?.validIss!!)
        assertIs<JWTToken>(idToken)
        assertIs<JWTToken>(accessToken)
        assertTrue(accessToken.validIss)

        val idClaims = challengeResponse.getIdClaims(serviceUrl)
        val accessClaims = challengeResponse.getAccessClaims(serviceUrl)
        assertIs<JWTPayload>(accessClaims)
        assertIs<JWTPayload>(idClaims)

        assertEquals("[\"openid\",\"email\",\"profile\"]", accessClaims.scope.toString())
        assertEquals("[\"http://localhost:8080/callback\"]", accessClaims.aud.toString())
        assertEquals("jane.doe@cryptr.co", accessClaims.email)
        assertEquals("okta", accessClaims.ips)
        assertEquals("creategram_9gJSUeWWmDKLVFRV5Vuavh", accessClaims.sci)
        assertEquals("cryptr|b2727127-5285-4e76-a8ae-41bdd062deaf", accessClaims.sub)
        assertEquals(2, accessClaims.ver)
        assertEquals("access", accessClaims.jtt)
        assertEquals(1698175518, accessClaims.exp)
        assertEquals(1698139518, accessClaims.iat)
        assertEquals("c6657a4b-3ea9-486a-a3b5-0f74a75df4e2", accessClaims.jti)
        assertEquals("a528c1b1-e37f-40b2-bae6-62a6ad950efb", accessClaims.cid)
        assertEquals("sandbox", accessClaims.env)
        assertEquals("creategram", accessClaims.org)
        assertEquals("a528c1b1-e37f-40b2-bae6-62a6ad950efb", accessClaims.azp)
        assertEquals("a528c1b1-e37f-40b2-bae6-62a6ad950efb", accessClaims.clientId)

        assertNull(idClaims.scope)
        assertEquals("\"a528c1b1-e37f-40b2-bae6-62a6ad950efb\"", idClaims.aud.toString())
        assertEquals("jane.doe@cryptr.co", idClaims.email)
        assertEquals("okta", idClaims.ips)
        assertEquals("creategram_9gJSUeWWmDKLVFRV5Vuavh", idClaims.sci)
        assertEquals("cryptr|b2727127-5285-4e76-a8ae-41bdd062deaf", idClaims.sub)
        assertEquals(2, idClaims.ver)
        assertEquals("openid", idClaims.jtt)
        assertEquals(1698175518, idClaims.exp)
        assertEquals(1698139518, idClaims.iat)
        assertEquals("a8ccc21c-4a0a-4b18-950e-1dabaca10089", idClaims.jti)
        assertEquals(0, idClaims.applicationMetadata?.keys?.size)
        assertNull(idClaims.cid)
        assertEquals("sandbox", idClaims.env)
        assertEquals("creategram", idClaims.org)
        assertNull(idClaims.azp)
        assertNull(idClaims.clientId)

        assertEquals(
            setOf("org_domain", "uid", "saml_subject", "last_name", "saml_nameid", "first_name", "email"),
            idClaims.metaData?.keys
        )

        assertEquals(setOf("given_name", "family_name"), idClaims.profile?.keys)

        assertEquals("false", idClaims.email_verified.toString())
        assertEquals("false", idClaims.phone_number_verified.toString())
    }


}