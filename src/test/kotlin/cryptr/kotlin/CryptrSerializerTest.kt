package cryptr.kotlin

import cryptr.kotlin.enums.EnvironmentStatus
import cryptr.kotlin.models.CryptrResource
import cryptr.kotlin.models.List
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.User
import cryptr.kotlin.models.connections.SSOConnection
import cryptr.kotlin.models.deleted.DeletedUser
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class CryptrSerializerTest {
    lateinit var cryptr: Cryptr
    lateinit var format: Json

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        val defaultRedirectUri = "http://localhost:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptr = Cryptr(tenantDomain, baseUrl, defaultRedirectUri, apiKeyClientId, apiKeyClientSecret)
        format = cryptr.format
        System.setProperty(
            "CRYPTR_API_KEY_TOKEN",
            "eyJ0eXAiOiJKV1QiLCJpc3MiOiJodHRwOi8vZGV2LmNyeXB0ci5ldS90L3NoYXJrLWFjYWRlbXkiLCJraWQiOiIxMjM0NTY3ODc5IiwiYWxnIjoiSFMyNTYifQ.eyJjaWQiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJkYnMiOiJzYW5kYm94IiwiZXhwIjoxOTg0MzEwNjQzLCJpYXQiOjE2ODQzMDcwNDMsImlzcyI6Imh0dHA6Ly9kZXYuY3J5cHRyLmV1L3Qvc2hhcmstYWNhZGVteSIsImp0aSI6ImFhMTM3NDI5LTE1NDgtNDRmMC04ZTY4LTk3ZDAzYzFkMDBmNyIsImp0dCI6ImFwaV9rZXkiLCJzY3AiOiJyZWFkX21hbnk6c3NvX2Nvbm5lY3Rpb25zIHVwZGF0ZTpzc29fY29ubmVjdGlvbnMiLCJzdWIiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJ0bnQiOiJzaGFyay1hY2FkZW15IiwidmVyIjoxfQ.q20l-u-8gjsHDkW1IQUErVdgGykWrZmiGaojMMfrVD4"
        )
    }

    @Test
    fun serializeOrganization() {
        val organizationJsonString = "{\n" +
                "  \"__type__\": \"Organization\",\n" +
                "  \"allowed_email_domains\": [\n" +
                "    \"cryptr.co\",\n" +
                "    \"muffun.com\",\n" +
                "    \"muffun.fr\"\n" +
                "  ],\n" +
                "  \"color\": \"green-400\",\n" +
                "  \"domain\": \"thibaud-java\",\n" +
                "  \"environments\": [\n" +
                "    {\n" +
                "      \"name\": \"sandbox\",\n" +
                "      \"status\": \"up\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"production\",\n" +
                "      \"status\": \"up\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"icon_logo_url\": null,\n" +
                "  \"inline_logo_url\": null,\n" +
                "  \"inserted_at\": \"2024-03-05T09:21:47\",\n" +
                "  \"locale\": null,\n" +
                "  \"name\": \"thibaud-java\",\n" +
                "  \"status\": {\n" +
                "    \"errors\": [],\n" +
                "    \"estimated_time_to_complete_in_seconds\": null,\n" +
                "    \"progress_in_percentage\": null,\n" +
                "    \"state\": \"terminated\"\n" +
                "  },\n" +
                "  \"timezone\": null,\n" +
                "  \"updated_at\": \"2024-04-04T12:02:46\"\n" +
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
            ?.let { assertEquals(EnvironmentStatus.UP, it.status) }

        assertEquals(
            JSONObject(organizationJsonString).keySet().sorted(),
            JSONObject(format.encodeToString(CryptrSerializer, organization)).keySet().sorted()
        )
    }

    @Test
    fun serializeUser() {
        val userJsonString = "{\n" +
                "  \"__domain__\": \"muffun\",\n" +
                "  \"__environment__\": \"sandbox\",\n" +
                "  \"__type__\": \"User\",\n" +
                "  \"active\": true,\n" +
                "  \"address\": null,\n" +
                "  \"email\": \"aryanna.stroman@gmail.com\",\n" +
                "  \"email_verified\": false,\n" +
                "  \"id\": \"60950f87-40f2-48f6-b6d7-18b9b9625d19\",\n" +
                "  \"identities\": [\n" +
                "    {\n" +
                "      \"idp_id\": \"muffun_2dGO7HYKXZjchhrCyLQmOLFKvlX\",\n" +
                "      \"authenticated_at\": \"2024-04-16T12:19:14\",\n" +
                "      \"provider\": \"saml.okta\",\n" +
                "      \"data\": {}\n" +
                "    }\n" +
                "  ],\n" +
                "  \"inserted_at\": \"2024-04-05T09:57:16\",\n" +
                "  \"meta_data\": [],\n" +
                "  \"phone_number\": null,\n" +
                "  \"phone_number_verified\": false,\n" +
                "  \"phone_numbers\": [],\n" +
                "  \"profile\": {\n" +
                "    \"birthdate\": null,\n" +
                "    \"family_name\": null,\n" +
                "    \"gender\": null,\n" +
                "    \"given_name\": \"Aryanna\",\n" +
                "    \"locale\": null,\n" +
                "    \"nickname\": null,\n" +
                "    \"picture\": null,\n" +
                "    \"preferred_username\": null,\n" +
                "    \"website\": null,\n" +
                "    \"zoneinfo\": null\n" +
                "  },\n" +
                "  \"updated_at\": \"2024-04-16T12:19:14\"\n" +
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
        assertNull(user.profile?.zoneInfo)

        assertEquals(
            JSONObject(userJsonString).keySet().sorted(),
            JSONObject(format.encodeToString(CryptrSerializer, user)).keySet().sorted()
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
                "    \"pagination\": {\n" +
                "        \"current_page\": 1,\n" +
                "        \"next_page\": 2,\n" +
                "        \"per_page\": 2,\n" +
                "        \"prev_page\": null,\n" +
                "        \"total_pages\": 12\n" +
                "    },\n" +
                "    \"total\": 23\n" +
                "}"

        val list = format.decodeFromString<List<CryptrResource>>(listingJsonString)

        assertEquals(23, list.total)
        assertEquals(1, list.pagination.currentPage)
        assertEquals(2, list.pagination.nextPage)
        assertEquals(2, list.pagination.perPage)
        assertNull(list.pagination.prevPage)
        assertEquals(12, list.pagination.totalPages)
        assertEquals(2, list.data.size)
        assertNull(list.resourceDomain)
    }

    @Test
    fun serializeDeleted() {
        val body = "{\n" +
                "  \"deleted\": true,\n" +
                "  \"resource\": {\n" +
                "    \"address\": null,\n" +
                "    \"email_verified\": false,\n" +
                "    \"__environment__\": \"sandbox\",\n" +
                "    \"profile\": {\n" +
                "      \"website\": null,\n" +
                "      \"zoneinfo\": null,\n" +
                "      \"birthdate\": null,\n" +
                "      \"gender\": null,\n" +
                "      \"nickname\": null,\n" +
                "      \"preferred_username\": null,\n" +
                "      \"given_name\": \"hamid\",\n" +
                "      \"locale\": null,\n" +
                "      \"family_name\": \"Echarkaoui\",\n" +
                "      \"picture\": null\n" +
                "    },\n" +
                "    \"phone_number_verified\": false,\n" +
                "    \"__domain__\": \"acme-company\",\n" +
                "    \"updated_at\": \"2023-02-23T18:33:28\",\n" +
                "    \"__type__\": \"User\",\n" +
                "    \"meta_data\": [\n" +
                "      {\n" +
                "        \"id\": \"46cd27a9-466a-4e2f-acb5-5af9acf4ec5d\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"email\",\n" +
                "          \"id\": \"f1acb4c6-a535-4026-b326-7c3d94bc9d18\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"8ce5248c-3158-4de7-a2df-92336b1d1427\",\n" +
                "        \"value\": \"hamid\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"first_name\",\n" +
                "          \"id\": \"ddf87f5d-db31-4da3-9a0f-9f552ad1a262\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"4d171054-4688-4017-9ec5-d5220bcfaec6\",\n" +
                "        \"value\": \"Echarkaoui\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"last_name\",\n" +
                "          \"id\": \"aa4ccab9-e471-4892-9fc5-868447360001\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"99bca1ff-66bf-4587-a8ba-1382e8b9cbcc\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"saml_nameid\",\n" +
                "          \"id\": \"3baaed8b-1670-406a-a1f0-506b873dfdc1\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"79a8515b-22f5-4538-9d3f-66577dc4c3a9\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"saml_subject\",\n" +
                "          \"id\": \"cd8e4eb3-684a-42b5-91f3-051e4792d683\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"d938f458-7e8b-4ce5-b044-a36aec31d4cf\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"uid\",\n" +
                "          \"id\": \"4f263c3b-63a4-4964-b1d5-7a47e6d50f26\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"phone_number\": null,\n" +
                "    \"id\": \"1e3c7c1e-a826-446f-8c9e-2a7e2374d805\",\n" +
                "    \"inserted_at\": \"2023-02-23T17:18:49\",\n" +
                "    \"email\": \"hamid@cryptr.co\"\n" +
                "  }\n" +
                "}"
        val decoded = cryptr.format.decodeFromString<DeletedUser>(body)
        assertIs<DeletedUser>(decoded)
        assertIs<User>(decoded.resource)
        assertTrue(decoded.deleted)
    }

    @Test
    fun serializeNotDeleted() {
        val body = "{\n" +
                "  \"deleted\": false,\n" +
                "  \"resource\": {\n" +
                "    \"address\": null,\n" +
                "    \"email_verified\": false,\n" +
                "    \"__environment__\": \"sandbox\",\n" +
                "    \"profile\": {\n" +
                "      \"website\": null,\n" +
                "      \"zoneinfo\": null,\n" +
                "      \"birthdate\": null,\n" +
                "      \"gender\": null,\n" +
                "      \"nickname\": null,\n" +
                "      \"preferred_username\": null,\n" +
                "      \"given_name\": \"hamid\",\n" +
                "      \"locale\": null,\n" +
                "      \"family_name\": \"Echarkaoui\",\n" +
                "      \"picture\": null\n" +
                "    },\n" +
                "    \"phone_number_verified\": false,\n" +
                "    \"__domain__\": \"acme-company\",\n" +
                "    \"updated_at\": \"2023-02-23T18:33:28\",\n" +
                "    \"__type__\": \"User\",\n" +
                "    \"meta_data\": [\n" +
                "      {\n" +
                "        \"id\": \"46cd27a9-466a-4e2f-acb5-5af9acf4ec5d\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"email\",\n" +
                "          \"id\": \"f1acb4c6-a535-4026-b326-7c3d94bc9d18\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"8ce5248c-3158-4de7-a2df-92336b1d1427\",\n" +
                "        \"value\": \"hamid\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"first_name\",\n" +
                "          \"id\": \"ddf87f5d-db31-4da3-9a0f-9f552ad1a262\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"4d171054-4688-4017-9ec5-d5220bcfaec6\",\n" +
                "        \"value\": \"Echarkaoui\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"last_name\",\n" +
                "          \"id\": \"aa4ccab9-e471-4892-9fc5-868447360001\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"99bca1ff-66bf-4587-a8ba-1382e8b9cbcc\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"saml_nameid\",\n" +
                "          \"id\": \"3baaed8b-1670-406a-a1f0-506b873dfdc1\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"79a8515b-22f5-4538-9d3f-66577dc4c3a9\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"saml_subject\",\n" +
                "          \"id\": \"cd8e4eb3-684a-42b5-91f3-051e4792d683\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"d938f458-7e8b-4ce5-b044-a36aec31d4cf\",\n" +
                "        \"value\": \"hamid@cryptr.co\",\n" +
                "        \"key\": {\n" +
                "          \"name\": \"uid\",\n" +
                "          \"id\": \"4f263c3b-63a4-4964-b1d5-7a47e6d50f26\",\n" +
                "          \"type\": \"string\",\n" +
                "          \"required\": false\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"phone_number\": null,\n" +
                "    \"id\": \"1e3c7c1e-a826-446f-8c9e-2a7e2374d805\",\n" +
                "    \"inserted_at\": \"2023-02-23T17:18:49\",\n" +
                "    \"email\": \"hamid@cryptr.co\"\n" +
                "  }\n" +
                "}"
        val decoded = cryptr.format.decodeFromString<DeletedUser>(body)
        assertIs<DeletedUser>(decoded)
        assertIs<User>(decoded.resource)
        assertFalse(decoded.deleted)
    }

    @Test
    fun serializeSSOConnection() {
        val body = "{\n" +
                "    \"__access__\": \"limited_to:factor\",\n" +
                "    \"__domain__\": \"factor\",\n" +
                "    \"__environment__\": \"production\",\n" +
                "    \"__managed_by__\": \"shark-academy\",\n" +
                "    \"__type__\": \"SSOConnection\",\n" +
                "    \"active\": true,\n" +
                "    \"default_redirection\": {\n" +
                "        \"__access__\": \"all_organizations_of:shark-academy\",\n" +
                "        \"__domain__\": \"shark-academy\",\n" +
                "        \"__environment__\": \"sandbox\",\n" +
                "        \"__managed_by__\": \"shark-academy\",\n" +
                "        \"__type__\": \"Redirection\",\n" +
                "        \"app_id\": \"863b0070-1a35-4261-91de-d27cc8c6b24a\",\n" +
                "        \"app_tenant_owner_id\": \"b0b0cc57-4580-4fc3-a5db-4dacc70cfe21\",\n" +
                "        \"id\": \"6b806720-5e6c-4498-a332-58e79e931832\",\n" +
                "        \"idp_id\": null,\n" +
                "        \"sp_id\": \"shark_academy_MgF6Z9maZKriEBbLM9KZJj\",\n" +
                "        \"uri\": \"http://localhost:4000\"\n" +
                "    },\n" +
                "    \"id\": \"factor_qYyGSpFJihHb7jJLf3agVo\",\n" +
                "    \"inserted_at\": \"2023-05-11T11:35:58\",\n" +
                "    \"metadata\": null,\n" +
                "    \"onboarding\": {\n" +
                "        \"__access__\": \"all_organizations_of:shark-academy\",\n" +
                "        \"__domain__\": \"shark-academy\",\n" +
                "        \"__environment__\": \"production\",\n" +
                "        \"__managed_by__\": \"shark-academy\",\n" +
                "        \"__type__\": \"EnterpriseConnectionOnboarding\",\n" +
                "        \"email_template_id\": null,\n" +
                "        \"id\": \"a6216afe-53ef-4a8e-bd62-07f49c7c825f\",\n" +
                "        \"inserted_at\": \"2023-05-11T11:35:58\",\n" +
                "        \"provider_type\": null,\n" +
                "        \"it_admin_email\": \"thibaud@cryptr.co\",\n" +
                "        \"state\": \"not_initialized\",\n" +
                "        \"tutorial_step\": 0,\n" +
                "        \"updated_at\": \"2023-05-11T11:35:58\"\n" +
                "    },\n" +
                "    \"provider_type\": \"unset\",\n" +
                "    \"seats_limit\": null,\n" +
                "    \"sp_id\": \"shark_academy_MgF6Z9maZKriEBbLM9KZJj\",\n" +
                "    \"updated_at\": \"2023-05-11T11:35:58\",\n" +
                "    \"user_security_type\": \"none\"\n" +
                "}"

        val decoded = cryptr.format.decodeFromString<SSOConnection>(body)
        assertIs<SSOConnection>(decoded)
    }

    @Test
    fun serializeOutput() {
        val body =
            "{\"__access__\":\"limited_to:factor\",\"__domain__\":\"factor\",\"__environment__\":\"production\",\"__managed_by__\":\"shark-academy\",\"__type__\":\"SSOConnection\",\"active\":true,\"default_redirection\":null,\"id\":\"factor_i8TQXJHEuMkFMoFaUCzxAA\",\"inserted_at\":\"2023-05-11T13:37:34\",\"metadata\":null,\"onboarding\":null,\"provider_type\":\"okta\",\"seats_limit\":null,\"sp_id\":\"shark_academy_MgF6Z9maZKriEBbLM9KZJj\",\"updated_at\":\"2023-05-11T13:37:34\",\"user_security_type\":\"none\"}"

        val decoded = cryptr.format.decodeFromString<SSOConnection>(body)
        assertIs<SSOConnection>(decoded)
    }
}