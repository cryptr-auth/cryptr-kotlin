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
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY29tbXVuaXRpei1hcHAuY3J5cHRyLmRldi90L211ZmZ1biIsImtpZCI6ImUyYWJjMDkzLWRkZGUtNDMyOC1hM2VlLTQ2MDEzNDg5YWYwMyIsInR5cCI6IkpXVCJ9.eyJhdWQiOltdLCJjbGllbnRfaWQiOiJlNjg5ODlhNS1iZDg3LTQ1Y2EtOGQwZS1kNWY5MWYyODU4NDkiLCJlbWFpbCI6InRoaWJhdWRAY3J5cHRyLmNvIiwiZW52Ijoic2FuZGJveCIsImV4cCI6MTcxMzI5MjM1NCwiaWF0IjoxNzEzMjU2MzU0LCJqdGkiOiI4MGEzMTllZi1kMTNhLTQ0ZGQtYTc1Ny1mYTUxMGVhMGE5MmUiLCJqdHQiOiJhY2Nlc3MiLCJvcmciOiJtdWZmdW4iLCJzY29wZSI6WyJvcGVuaWQiLCJlbWFpbCIsInByb2ZpbGUiXSwic3ViIjoiY3J5cHRyfDYwOTUwZjg3LTQwZjItNDhmNi1iNmQ3LTE4YjliOTYyNWQxOSIsInZlciI6M30.N95PnaexgwZXVRbfjMtChwNgBdpGE8-avtHaxK9B1jn4_Q0GKOQA_RRrHwjbSRMC-yBI7fo7EDBH3D-SSHMvWkA-GtYMDro_ByH_oNlGKlx5cpJftecXAXtm3Xg_ZnYMheshwH471o-N-aHEo777z20LCZz85wEEgR1gqM7cK5kIRenmcOX91moU9SJPuEkDgmXtAtv_ihJmMgAGBnw1n9JTF2ECvDneHJLC0mL8Co5IOuGgJjprIigq3yr8OauIgBsYXhEyXYWcdjkG0nZK6mKR5AT9Yt6fBho666mPpNi2C1kNfIsFiWscXT4YFEO-54nKiSLbTiEojYea53NpYbqo2jgk48eMwy_5wmjNmXhHrZ4nZJReT5Iv-4QklQAdufdso2GpXwLi7XH7xUnTDvArUSuOWLCGEcbbxpH5C6qORKFirsOs0GdSSVRhJpAo2AmakaBB2jc090SUZZT5JJ8-Q3sgMFUk4SFWFuMeu6sf6RS8kdpEo97Z1NBowhsEnYFv8EbjM4k7D5uMxgasztgiPPNKFnMBugom0k_gq7txb75TWT7SzCFoOq4ftHO9g50WLlEknqPT8Mjg88tprxqe9iE_U5lIuzOPecAGeIxWOevzALKNfPtcTMFPtMw1SSfDxFwgvlpUmYZElIv6MqGdgsaEFCwZ6yQvAydH4-s"
    private var v2IdToken =
        "eyJhbGciOiJSUzI1NiIsImlzcyI6Imh0dHBzOi8vY29tbXVuaXRpei1hcHAuY3J5cHRyLmRldi90L211ZmZ1biIsImtpZCI6ImUyYWJjMDkzLWRkZGUtNDMyOC1hM2VlLTQ2MDEzNDg5YWYwMyIsInR5cCI6IkpXVCJ9.eyJhdF9oYXNoIjoiSmFBMGlIWnBLdHJ4dVJQSWxId1R4dyIsImF1ZCI6ImU2ODk4OWE1LWJkODctNDVjYS04ZDBlLWQ1ZjkxZjI4NTg0OSIsImNfaGFzaCI6IlpUQ3NmT1B4VGNVM1pEUnhPZTVVSVEiLCJlbWFpbCI6InRoaWJhdWRAY3J5cHRyLmNvIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJlbnYiOiJzYW5kYm94IiwiZXhwIjoxNzEzMjkyMzU0LCJpYXQiOjE3MTMyNTYzNTQsImlkZW50aXRpZXMiOlt7ImlkcF9pZCI6Im11ZmZ1bl8yZEdPN0hZS1haamNoaHJDeUxRbU9MRkt2bFgiLCJhdXRoZW50aWNhdGVkX2F0IjoxNzEzMjU2MzU0LCJwcm92aWRlciI6InNhbWwub2t0YSIsImRhdGEiOnt9fV0sImp0aSI6IjYyYTRmOTRjLTFhNWUtNDM4OS04NTFhLTFjMzYxM2U5MDM5OCIsImp0dCI6Im9wZW5pZCIsIm1ldGFfZGF0YSI6e30sIm9yZyI6Im11ZmZ1biIsInBob25lX251bWJlcl92ZXJpZmllZCI6ZmFsc2UsInByb2ZpbGUiOnsiZmFtaWx5X25hbWUiOiJSZW5hdXgiLCJnaXZlbl9uYW1lIjoiVGhpYmF1ZCJ9LCJzdWIiOiJjcnlwdHJ8NjA5NTBmODctNDBmMi00OGY2LWI2ZDctMThiOWI5NjI1ZDE5IiwidmVyIjozfQ.NrobMJrAdEm9hJlMZvw1T6bshDtcY4AvZWWvGmR_4aghxqC36aTLC_TFe-4CayOENgtfjok8FlAB2NbxbyvLWMUuKIrzo6YkU6aPCcfJZgBNWtwgv-yH0CsK_7aUs_7cHw_T4m8Bh9Oomn-GUXBPVGhMPs6mjtkyAL_BNuvbzp96zOgmly91npMEOvqeUzL7u-hGw67NMIew77PAsht0q2ba04MdlurhG8M9Q9Gqn6_fQMRcIIKtL_Jz8n2UAp7SuJGQhEiTqJtsTRx2RJ4mk12Ll5Bxky8GZqe4HZDpyYScPiQUdh9gCeH-U4SkxTFEsm50fWHnWaXu29ohqan5C9ZAKyjicUmlkTVFyIzFvuyHrd_5tNnVdmJT8GZKTnj6FX7Sa4AzLQuvA512xE4_1So2bgTzeLHGNWZxYWtqizCSJ76T5mndBJRmFTMrMjfI5Ag01w2T1hxif8t6s8fSGWgD_FIQ-dJVQno9iZaiId_laRhaK_diLdDRgRXxTNt8HZGww6NTmlHwZvgdjnqpjznzXbqiXYUlVAFsCSEkriT0d3G7gREzvpAmLFAkW5k_L2dM5vf59A_ZXBFqzCIMiLsZeDeWda4EJ_H8ohKghdTH-K32s-jhK-3phi8dhYFkp5NsPMTC1rx-YhyehaxetiQutfbLamt0033BbRcRCKk"

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
        assertEquals(1713292354, accessClaims.exp)
        assertEquals(1713256354, accessClaims.iat)
        assertEquals("80a319ef-d13a-44dd-a757-fa510ea0a92e", accessClaims.jti)
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
        assertEquals(1713292354, idClaims.exp)
        assertEquals(1713256354, idClaims.iat)
        assertEquals("62a4f94c-1a5e-4389-851a-1c3613e90398", idClaims.jti)
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
                1713256354,
                "saml.okta",
                mapOf<String, JsonElement>()
            ),
            idClaims.identities?.first()
        )

        assertEquals("false", idClaims.email_verified.toString())
        assertEquals("false", idClaims.phone_number_verified.toString())
    }


}