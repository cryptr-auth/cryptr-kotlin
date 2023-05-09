package cryptr.kotlin

import cryptr.kotlin.enums.ApplicationType
import cryptr.kotlin.enums.EnvironmentStatus
import cryptr.kotlin.models.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CryptrSerializerTest {
    lateinit var cryptrApi: CryptrAPI
    lateinit var format: Json

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        val defaultRedirectUrl = "http://localhost:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptrApi = CryptrAPI(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret)
        format = cryptrApi.format
        System.setProperty("CRYPTR_API_KEY_TOKEN", "stored-api-key")
    }

    @Test
    fun serializeOrganization() {
        val organizationJsonString = "{\n" +
                "    \"__type__\": \"Organization\",\n" +
                "    \"domain\": \"thibaud-java\",\n" +
                "    \"environments\": [\n" +
                "        {\n" +
                "            \"name\": \"production\",\n" +
                "            \"status\": \"down\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"sandbox\",\n" +
                "            \"status\": \"up\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"inserted_at\": \"2023-04-27T13:50:29\",\n" +
                "    \"name\": \"thibaud-java\",\n" +
                "    \"updated_at\": \"2023-04-27T13:50:49\"\n" +
                "}"

        val organization = format.decodeFromString<Organization>(organizationJsonString)
        assertEquals("thibaud-java", organization.domain)
        assertEquals("thibaud-java", organization.name)
        assertEquals("Organization", organization.cryptrType)
        assertEquals(2, organization.environments.size)
        assertContains(organization.environments.map { e -> e.name }, "production")
        assertContains(organization.environments.map { e -> e.name }, "sandbox")
        organization.environments.find { it.name == "sandbox" }
            ?.let { assertEquals(EnvironmentStatus.UP, it.status) }
        organization.environments.find { it.name == "production" }
            ?.let { assertEquals(EnvironmentStatus.DOWN, it.status) }

        assertEquals(
            JSONObject(organizationJsonString).keySet().sorted(),
            JSONObject(format.encodeToString(CryptrSerializer, organization)).keySet().sorted()
        )
    }

    @Test
    fun serializeUser() {
        val userJsonString = "{\n" +
                "    \"__domain__\": \"acme-company\",\n" +
                "    \"__environment__\": \"sandbox\",\n" +
                "    \"__type__\": \"User\",\n" +
                "    \"address\": null,\n" +
                "    \"email\": \"aryanna.stroman@gmail.com\",\n" +
                "    \"email_verified\": false,\n" +
                "    \"id\": \"9ef8cc11-40e0-432a-8816-6a3b5034519f\",\n" +
                "    \"inserted_at\": \"2023-05-03T14:03:09\",\n" +
                "    \"meta_data\": [],\n" +
                "    \"phone_number\": null,\n" +
                "    \"phone_number_verified\": false,\n" +
                "    \"profile\": {\n" +
                "        \"birthdate\": null,\n" +
                "        \"family_name\": null,\n" +
                "        \"gender\": null,\n" +
                "        \"given_name\": \"Aryanna\",\n" +
                "        \"locale\": null,\n" +
                "        \"nickname\": null,\n" +
                "        \"picture\": null,\n" +
                "        \"preferred_username\": null,\n" +
                "        \"website\": null,\n" +
                "        \"zoneinfo\": null\n" +
                "    },\n" +
                "    \"updated_at\": \"2023-05-03T14:03:09\"\n" +
                "}"

        val user = format.decodeFromString<User>(userJsonString)
        assertEquals("User", user.cryptrType)
        assertEquals("aryanna.stroman@gmail.com", user.email)
        assertNull(user.profile?.birthdate)
        assertNull(user.address)
        assertNull(user.profile?.familyName)
        assertNull(user.profile?.gender)
        assertEquals("Aryanna", user.profile?.givenName)
        assertNull(user.profile?.locale)
        assertNull(user.profile?.nickname)
        assertNull(user.phoneNumber)
        assertNull(user.profile?.picture)
        assertNull(user.profile?.website)
        assertNull(user.profile?.zoneinfo)

        assertEquals(
            JSONObject(userJsonString).keySet().sorted(),
            JSONObject(format.encodeToString(CryptrSerializer, user)).keySet().sorted()
        )
    }

    @Test
    fun serializeApplication() {
        val applicationJsonString = "{\n" +
                "    \"__domain__\": \"acme-company\",\n" +
                "    \"__environment__\": \"sandbox\",\n" +
                "    \"__type__\": \"Application\",\n" +
                "    \"allowed_logout_urls\": [\n" +
                "        \"https://communitiz-app-vuejs.onrender.com\"\n" +
                "    ],\n" +
                "    \"allowed_origins_cors\": [\n" +
                "        \"https://communitiz-app-vuejs.onrender.com\"\n" +
                "    ],\n" +
                "    \"allowed_redirect_urls\": [\n" +
                "        \"https://communitiz-app-vuejs.onrender.com\"\n" +
                "    ],\n" +
                "    \"application_type\": \"ruby_on_rails\",\n" +
                "    \"client_id\": \"bc3583eb-59e3-4edf-83c4-96bd308430cc\",\n" +
                "    \"default_origin_cors\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                "    \"default_redirect_uri_after_login\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                "    \"default_redirect_uri_after_logout\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                "    \"description\": null,\n" +
                "    \"id\": \"bc3583eb-59e3-4edf-83c4-96bd308430cc\",\n" +
                "    \"inserted_at\": \"2023-05-02T16:06:47\",\n" +
                "    \"name\": \"Community App Communitiz Real QA App\",\n" +
                "    \"updated_at\": \"2023-05-02T16:06:47\"\n" +
                "}"

        val application = format.decodeFromString<Application>(applicationJsonString)
        assertEquals("Application", application.cryptrType)
        assertEquals("sandbox", application.environment)
        assertEquals("acme-company", application.resourceDomain)
        assertEquals(setOf("https://communitiz-app-vuejs.onrender.com"), application.allowedLogoutUrls)
        assertEquals(setOf("https://communitiz-app-vuejs.onrender.com"), application.allowedOriginsCors)
        assertEquals(setOf("https://communitiz-app-vuejs.onrender.com"), application.allowedRedirectUrls)
        assertEquals(ApplicationType.RUBY_ON_RAILS, application.applicationType)
        assertEquals("bc3583eb-59e3-4edf-83c4-96bd308430cc", application.clientId)
        assertEquals("https://communitiz-app-vuejs.onrender.com", application.defaultOriginCors)
        assertEquals("https://communitiz-app-vuejs.onrender.com", application.defaultRedirectUriAfterLogin)
        assertEquals("https://communitiz-app-vuejs.onrender.com", application.defaultRedirectUriAfterLogout)
        assertNull(application.description)
        assertEquals("bc3583eb-59e3-4edf-83c4-96bd308430cc", application.id)
        assertNotNull(application.insertedAt)
        assertNotNull(application.updatedAt)
        assertEquals("Community App Communitiz Real QA App", application.name)


        assertEquals(
            JSONObject(applicationJsonString).keySet(),
            JSONObject(format.encodeToString(CryptrSerializer, application)).keySet()
        )
    }

    @Test
    fun serializeList() {
        val listingJsonString = "{\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"__type__\": \"Organization\",\n" +
                "            \"domain\": \"thibaud-paco\",\n" +
                "            \"environments\": [\n" +
                "                {\n" +
                "                    \"id\": \"57f6e6a5-e833-49c5-8172-a94ec7a91b50\",\n" +
                "                    \"name\": \"production\",\n" +
                "                    \"status\": \"down\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"40ce67ff-baa5-49bb-b20c-7ddefc7e205e\",\n" +
                "                    \"name\": \"sandbox\",\n" +
                "                    \"status\": \"down\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"inserted_at\": \"2023-04-27T13:54:42\",\n" +
                "            \"name\": \"Thibaud Paco\",\n" +
                "            \"updated_at\": \"2023-04-27T13:55:03\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"__type__\": \"Organization\",\n" +
                "            \"domain\": \"thibaud-java\",\n" +
                "            \"environments\": [\n" +
                "                {\n" +
                "                    \"id\": \"7389e2d4-e49c-4371-8bfb-1f6bc243fe74\",\n" +
                "                    \"name\": \"production\",\n" +
                "                    \"status\": \"down\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": \"f3751057-724d-41e8-9057-73067d46e715\",\n" +
                "                    \"name\": \"sandbox\",\n" +
                "                    \"status\": \"down\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"inserted_at\": \"2023-04-27T13:50:29\",\n" +
                "            \"name\": \"thibaud-java\",\n" +
                "            \"updated_at\": \"2023-04-27T13:50:49\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"paginate\": {\n" +
                "        \"current_page\": 1,\n" +
                "        \"next_page\": 2,\n" +
                "        \"per_page\": 2,\n" +
                "        \"prev_page\": null,\n" +
                "        \"total_pages\": 12\n" +
                "    },\n" +
                "    \"total\": 23\n" +
                "}"

        val listing = format.decodeFromString<Listing<CryptrResource>>(listingJsonString)

        assertEquals(23, listing.total)
        assertEquals(1, listing.pagination.currentPage)
        assertEquals(2, listing.pagination.nextPage)
        assertEquals(2, listing.pagination.perPage)
        assertNull(listing.pagination.prevPage)
        assertEquals(12, listing.pagination.totalPages)
        assertEquals(2, listing.data.size)
        assertNull(listing.resourceDomain)
    }
}