package cryptr.kotlin.models

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
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY29tbXVuaXRpei1hcHAuY3J5cHRyLmRldi90L211ZmZ1biIsImtpZCI6ImUyYWJjMDkzLWRkZGUtNDMyOC1hM2VlLTQ2MDEzNDg5YWYwMyIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJjbGllbnRfaWQiOiJlNjg5ODlhNS1iZDg3LTQ1Y2EtOGQwZS1kNWY5MWYyODU4NDkiLCJlbWFpbCI6InRoaWJhdWRAY3J5cHRyLmNvIiwiZW52Ijoic2FuZGJveCIsImV4cCI6MTcxMzIxOTI2NSwiaWF0IjoxNzEzMTgzMjY1LCJqdGkiOiJlNTY0ZjdmMS1jZWQ3LTRjNzAtOWI3Yy1iODQzM2MxZTkxMmEiLCJqdHQiOiJhY2Nlc3MiLCJvcmciOiJtdWZmdW4iLCJzY29wZSI6WyJvcGVuaWQiLCJlbWFpbCIsInByb2ZpbGUiXSwic3ViIjoiY3J5cHRyfDYwOTUwZjg3LTQwZjItNDhmNi1iNmQ3LTE4YjliOTYyNWQxOSIsInZlciI6M30.q6NRswyedhn_BiRkNQkz9IPyyQiEOr2h7XPqyafRUbmAZYWomivHk5oSi57N3W0ERaCr8jCgjS3opRqR0C2Kx-msDz52erTeYvk3FmR0bn4cdzkGLZR9qRI2xghZ1WRuAiKCZaHP9BjVGpXhfFpLbv9eY3UOE6pubw7GWjb1GMLgHO31cNlIGa97LnsDnDhvPAzg_UDUFBUcMhnk0PLCSB25cpAZ4MtNZBSGS4WbY03aX0LmNgjKArv9-txkqHjGBcU4jJVnqYpfnT7Tv_vDD7v0f3YaiWOvaGBVlhfaDXj2l_byIp4stxsva7UXWS2W5uyk-1QUo3BaXAUPVVO1ggqBPb-1EIOc98KoswZynnX7WcQu8MBuwFk-Yul-3v1dZYM5_ARe0PfMXfaR66WZSDBWaUJtxVAUXZ8HFGszsH3AIICb0sVoVAyed9cP1G7RyswATXreMXhOLGZ2MHwlaSkSA34DhqC-pvRHpa5dbQ_NhUHAxlXYfEOn85_zKR-6F6AFgk9Tim-LAhFj6DQ7zYabfEarJ2BJhWOjxIh4GH-O8CuEJlev5FqRItRqAp6FHz5359PGFnPy4ByEkz_pBqAGmyDtDeUvacWumtTDDSv7mU60-eJaa-1BlSqik_4bgoCNfkzWLs4JmWSDOgbba14EXGoAwzv6x4rQXsh5MiY"
    private var v2IdToken =
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY29tbXVuaXRpei1hcHAuY3J5cHRyLmRldi90L211ZmZ1biIsImtpZCI6ImUyYWJjMDkzLWRkZGUtNDMyOC1hM2VlLTQ2MDEzNDg5YWYwMyIsInR5cCI6IkpXVCJ9.eyJhdF9oYXNoIjoiY1lPWU11c19kRWRGZTZvZ2x2cUJVUSIsImF1ZCI6ImU2ODk4OWE1LWJkODctNDVjYS04ZDBlLWQ1ZjkxZjI4NTg0OSIsImNfaGFzaCI6InJXTzhvbUlmUXFXQjNMRWNGZHJfcFEiLCJlbWFpbCI6InRoaWJhdWRAY3J5cHRyLmNvIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJlbnYiOiJzYW5kYm94IiwiZXhwIjoxNzEzMjE5MjY1LCJpYXQiOjE3MTMxODMyNjUsImlkZW50aXRpZXMiOlt7ImlkcF9pZCI6Im11ZmZ1bl8yZEdPN0hZS1haamNoaHJDeUxRbU9MRkt2bFgiLCJhdXRoZW50aWNhdGVkX2F0IjoxNzEzMTgzMjY1LCJwcm92aWRlciI6InNhbWwub2t0YSIsImRhdGEiOnt9fV0sImp0aSI6IjIzMTAxOGZkLTU3ZmItNGUyMi1iNjcwLTU3YzUzMjZiOTA3MSIsImp0dCI6Im9wZW5pZCIsIm1ldGFfZGF0YSI6e30sIm9yZyI6Im11ZmZ1biIsInBob25lX251bWJlcl92ZXJpZmllZCI6ZmFsc2UsInByb2ZpbGUiOnsiZmFtaWx5X25hbWUiOiJSZW5hdXgiLCJnaXZlbl9uYW1lIjoiVGhpYmF1ZCJ9LCJzdWIiOiJjcnlwdHJ8NjA5NTBmODctNDBmMi00OGY2LWI2ZDctMThiOWI5NjI1ZDE5IiwidmVyIjozfQ.r7obibQnatKc13vdL9sEtaQbYR0CXZCNOcC8fiWX9iOlivnJUxsN9uFEXhpKU9U3Q7BfWeNW9Q5WWOO-1pKCT7EmcqaZEjmaj0lG9pE4cSjNHWr-xLW1cydVKaUssCIPAlHmNJiLaA0cT7H8CQd38WZa1sgZvU_Nj1dISJosQufv4dlhPAIbbo5wdGnzrt3qowCAEquqyE4ncHGLVUtmv3-oYYEMDR1wpsPb3OZ_5fU-4e5VBorCqOeyYN8hDf36kEfodEBwXtbBlF45KO-Xpi_1-VC7S42WvkiucYaX0nOKvqOvRVfDdge7XNO3g8g3rPjRkUZTgoZr9zb462tKNhNfYZbu1DQKi5GBzfI0dsAFBshRslGB9dmrcNdmQ4mLmj5eklTxPjf8NVwofn53GNFEfKdWy_AbfiiDMp953MOMHvjbNj1xgoBQ8Q5gUUCgh7A9IiINdZgY85INHHd07Pgeg7YTR-BzQFl8AkoUVjMypridSgOdYS7UEFur6FPBa0_hXZ-3IaE2KYiXD2h230sa7Shew1rlQytVkNGwvJXfoL6bYecz7y25m3LkC6ayvf29wrnBhk-mag0obf-2KOhv8DyfY8SPAK8IBSKn3YkpK1y4KGEGoK2puJLlLns9g9Qj_OYyRyD5QzbKZCaLBMLpmAF6QKmKH_1spFqcPTc"

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
        assertEquals("cryptr|60950f87-40f2-48f6-b6d7-18b9b9625d19", accessClaims.sub)
        assertEquals(3, accessClaims.ver)
        assertEquals("access", accessClaims.jtt)
        assertEquals(1713219265, accessClaims.exp)
        assertEquals(1713183265, accessClaims.iat)
        assertEquals("e564f7f1-ced7-4c70-9b7c-b8433c1e912a", accessClaims.jti)
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
        assertEquals("cryptr|60950f87-40f2-48f6-b6d7-18b9b9625d19", idClaims.sub)
        assertEquals(3, idClaims.ver)
        assertEquals("openid", idClaims.jtt)
        assertEquals(1713219265, idClaims.exp)
        assertEquals(1713183265, idClaims.iat)
        assertEquals("231018fd-57fb-4e22-b670-57c5326b9071", idClaims.jti)
        assertNull(idClaims.applicationMetadata)
        //assertNull(idClaims.cid)
        assertEquals("sandbox", idClaims.env)
        assertEquals("muffun", idClaims.org)
        //assertNull(idClaims.azp)
        assertNull(idClaims.clientId)

        assertEquals(setOf(), idClaims.metaData?.keys)

        assertEquals(setOf("given_name", "family_name"), idClaims.profile?.keys)
        assertEquals(
            Identity("muffun_2dGO7HYKXZjchhrCyLQmOLFKvlX", 1713183265, "saml.okta", mapOf<String, JsonElement>()),
            idClaims.identities?.first()
        )

        assertEquals("false", idClaims.email_verified.toString())
        assertEquals("false", idClaims.phone_number_verified.toString())
    }


}