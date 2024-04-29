package cryptr.kotlin.models

import cryptr.kotlin.models.jwt.JWTIdentity
import cryptr.kotlin.models.jwt.JWTPayload
import cryptr.kotlin.models.jwt.JWTToken
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class ChallengeResponseTest {

    @BeforeEach
    fun init() {
        System.setProperty("CRYPTR_JWT_ALG", "RS256")
    }

    private var serviceUrl = "https://communitiz-app.cryptr.dev"
    private var v2AccessToken =
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY29tbXVuaXRpei1hcHAuY3J5cHRyLmRldi90L211ZmZ1biIsImtpZCI6ImUyYWJjMDkzLWRkZGUtNDMyOC1hM2VlLTQ2MDEzNDg5YWYwMyIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJjbGllbnRfaWQiOiJlNjg5ODlhNS1iZDg3LTQ1Y2EtOGQwZS1kNWY5MWYyODU4NDkiLCJlbWFpbCI6InRoaWJhdWRAY3J5cHRyLmNvIiwiZW52Ijoic2FuZGJveCIsImV4cCI6MjAyOTc1NDk1NywiaWF0IjoxNzE0Mzk1MDA1LCJqdGkiOiIwZDQ5OWI3MS1lNDNmLTQ5OGEtYjY0Mi1mMzhjNTQwNDI1ZmQiLCJqdHQiOiJhY2Nlc3MiLCJvcmciOiJtdWZmdW4iLCJzY29wZSI6WyJvcGVuaWQiLCJlbWFpbCIsInByb2ZpbGUiXSwic3ViIjoib2t0YXw2MDk1MGY4Ny00MGYyLTQ4ZjYtYjZkNy0xOGI5Yjk2MjVkMTkiLCJ2ZXIiOjN9.YFwXeioYvxLvB368R9T4ioOGm9bcnh8UDqcJJHXyD8jQ64Ext945oaGxfLv_mYLtTVXWNVbrgmN6SHoxaZxwuZa8qBZsu-5TU9QispEeZDAleoca9teRu3SzViL3p96Ctwd8ugH4k81KXYY2wzRrnaNFHyOTaWEbvtFuu6rtQ2Lt2-IDzcnc5hAQUEY-39aX-NoYwby-tHEG-F0swtbbrUzwMGFxsxRwbsZ1_ruhzXBLMSVm7zrnZ5TBsyzlMomikuGbucNDSYyTWe8uzp8S87Zavd93eZi4Tz1dPcV9L9mqCrQXxAEyZdeOJj4t-1KyFy3leiLCpsV8CYwPS23i54DrEAd7Sp-MvuTiULftf4MpiPJ-DfJB8Z58GhmPivxE0lrioKZrocniQmQwE0uwppYxAjYaUeIO-pabHz5llW_MPyFoGaCPyLlFuXJXhcOEA_Rb85Ahu08RScGnXHPeheKw3drKIVqjBc6jAuc4IMj5LcdLhuimY5GoJiIqtjLU1zPHfqquxsf0oweLEUWA3-vEiPuOfiAIYJiGVPl8IFjFKEuJnkAs1Srfns1Q6xobklOIdLPV4S6MmqZslSAeLaTIvMXy2JyXNnjy-9tJ3MFoGnQwsvGIrUoKrfSt4kOaRQEM3YtoXw4dPNKaW11VJgaG7J_C8hQ9NvpihUCwwGw"
    private var v2IdToken =
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY29tbXVuaXRpei1hcHAuY3J5cHRyLmRldi90L211ZmZ1biIsImtpZCI6ImUyYWJjMDkzLWRkZGUtNDMyOC1hM2VlLTQ2MDEzNDg5YWYwMyIsInR5cCI6IkpXVCJ9.eyJhdF9oYXNoIjoiN2t5Ri1lQTJVZTNVNUV6dDdlZ21wUSIsImF1ZCI6ImU2ODk4OWE1LWJkODctNDVjYS04ZDBlLWQ1ZjkxZjI4NTg0OSIsImNfaGFzaCI6InMzaWFpYjRuY0hpckVEUXo1YUpRbEEiLCJlbWFpbCI6InRoaWJhdWRAY3J5cHRyLmNvIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJlbnYiOiJzYW5kYm94IiwiZXhwIjoyMDI5NzU0OTU3LCJpYXQiOjE3MTQzOTUwMDUsImlkZW50aXRpZXMiOlt7ImlkcF9pZCI6Im11ZmZ1bl8yZEdPN0hZS1haamNoaHJDeUxRbU9MRkt2bFgiLCJhdXRoZW50aWNhdGVkX2F0IjoxNzEzNTEzNzM0LCJwcm92aWRlciI6InNhbWwub2t0YSIsImRhdGEiOnt9fV0sImp0aSI6ImQwYTA5ZWM0LTA0NmItNGM3ZC1iN2NmLTdhYjA4YWVkMWY5YyIsImp0dCI6Im9wZW5pZCIsIm1ldGFfZGF0YSI6e30sIm9yZyI6Im11ZmZ1biIsInBob25lX251bWJlcl92ZXJpZmllZCI6ZmFsc2UsInByb2ZpbGUiOnsiZmFtaWx5X25hbWUiOiJSZW5hdXgiLCJnaXZlbl9uYW1lIjoiVGhpYmF1ZCJ9LCJzdWIiOiJva3RhfDYwOTUwZjg3LTQwZjItNDhmNi1iNmQ3LTE4YjliOTYyNWQxOSIsInZlciI6M30.BwQGJy4phmOCJNsZ_WRGS1wLxsMwUIAK6yXAFZMI9VqlNra6j-a2Vo_4mKhtohPJJmWpKT217ptSYb2ZHX3dj1FLvbkhoORw2PLa1_aSTW2O6ucXoZsDPgDV0AQxsxfdPtYBLRLMNc4FBq_d2X9_UgjgeQ_Z49syygCNKv-dmkHn7Hh_mQFOKrYeDimuLBsdYMCJ77nd1ITVV2USOEeAWmgKHiB5MRd6OCAaTNFnS3WOKPH9KhsvTVjivYSkBwgUCqwamM1oIW7v1cd4HqWJB-p3iV8ZcdzOOJmwFgpw8zUg_KJPd10dp1-S-pnNGs8Al7MK32xUYxC2rB_c3swZBeCqlP2JiR-H_HOllKh9-HzAcEbvSqfZNkA_UpjehOwYJoKeg31ds0IuwfEGJ-QinJyQqLnjggCyzR-qrBcUyxgon4hvaHK6NzLSLJKKEdV24t4c75gBlIeJCCGHYqp77zfDbd9Dtum-VtDSrWiWkI-bvlvy9Jx2PsFQsQPxEwqJ65RykLt-yKfUESgg-cq2Ur4oGfuyFTNlNy5qaoa49PoC_YRN0SdJYws-5ucBk-l4ctGE8QCwZcEMh8o0y8HQattHumpSXSOoFtLJ3h8MMOp6RiujRccZd4J9ObGaSK2S9pO91Ep1yvHdNEHkue4oF6uEokoTpEkRjGKyFd15Kpo"

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
        assertTrue(idToken.validIss)
        assertIs<JWTToken>(idToken)
        assertIs<JWTToken>(accessToken)
        assertTrue(accessToken.validIss)

        val idClaims = challengeResponse.getIdClaims(serviceUrl)
        val accessClaims = challengeResponse.getAccessClaims(serviceUrl)
        assertIs<JWTPayload>(accessClaims)
        assertIs<JWTPayload>(idClaims)

        assertEquals("[\"openid\",\"email\",\"profile\"]", accessClaims.scope.toString())
        assertEquals("[]", accessClaims.aud.toString())
        assertEquals("thibaud@cryptr.co", accessClaims.email)
        //assertEquals("okta", accessClaims.ips)
        //assertEquals("muffun_2dGO7HYKXZjchhrCyLQmOLFKvlX", accessClaims.sci)
        assertEquals("okta|60950f87-40f2-48f6-b6d7-18b9b9625d19", accessClaims.sub)
        assertEquals(3, accessClaims.ver)
        assertEquals("access", accessClaims.jtt)
        assertEquals(2029754957, accessClaims.exp)
        assertEquals(1714395005, accessClaims.iat)
        assertEquals("0d499b71-e43f-498a-b642-f38c540425fd", accessClaims.jti)
        //assertEquals("a528c1b1-e37f-40b2-bae6-62a6ad950efb", accessClaims.cid)
        assertEquals("sandbox", accessClaims.env)
        assertEquals("muffun", accessClaims.org)
        //assertEquals("a528c1b1-e37f-40b2-bae6-62a6ad950efb", accessClaims.azp)
        assertEquals("e68989a5-bd87-45ca-8d0e-d5f91f285849", accessClaims.clientId)

        assertNull(idClaims.scope)
        assertEquals("\"e68989a5-bd87-45ca-8d0e-d5f91f285849\"", idClaims.aud.toString())
        assertEquals("thibaud@cryptr.co", idClaims.email)
        //assertEquals("okta", idClaims.ips)
        //assertEquals("creategram_9gJSUeWWmDKLVFRV5Vuavh", idClaims.sci)
        assertEquals("okta|60950f87-40f2-48f6-b6d7-18b9b9625d19", idClaims.sub)
        assertEquals(3, idClaims.ver)
        assertEquals("openid", idClaims.jtt)
        assertEquals(2029754957, idClaims.exp)
        assertEquals(1714395005, idClaims.iat)
        assertEquals("d0a09ec4-046b-4c7d-b7cf-7ab08aed1f9c", idClaims.jti)
        assertNull(idClaims.applicationMetadata)
        //assertNull(idClaims.cid)
        assertEquals("sandbox", idClaims.env)
        assertEquals("muffun", idClaims.org)
        //assertNull(idClaims.azp)
        assertNull(idClaims.clientId)

        assertEquals(setOf(), idClaims.metaData?.keys)

        assertEquals(setOf("given_name", "family_name"), idClaims.profile?.keys)
        assertEquals(
            JWTIdentity(
                "muffun_2dGO7HYKXZjchhrCyLQmOLFKvlX",
                1713513734,
                "saml.okta",
                mapOf<String, JsonElement>()
            ),
            idClaims.identities?.first()
        )

        assertEquals("false", idClaims.email_verified.toString())
        assertEquals("false", idClaims.phone_number_verified.toString())
    }


}