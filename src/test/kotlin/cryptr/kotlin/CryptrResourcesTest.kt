package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.enums.EnvironmentStatus
import cryptr.kotlin.models.*
import cryptr.kotlin.models.List
import cryptr.kotlin.models.connections.PasswordConnection
import cryptr.kotlin.models.deleted.DeletedUser
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.Normalizer
import kotlin.test.*

@WireMockTest(proxyMode = true)
class CryptrResourcesTest {
    lateinit var cryptr: Cryptr

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        val defaultRedirectUri = "http://localhost:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptr = Cryptr(tenantDomain, baseUrl, defaultRedirectUri, apiKeyClientId, apiKeyClientSecret)
        System.setProperty("CRYPTR_JWT_ALG", "HS256")
        System.setProperty(
            "CRYPTR_API_KEY_TOKEN",
            "eyJ0eXAiOiJKV1QiLCJpc3MiOiJodHRwOi8vZGV2LmNyeXB0ci5ldS90L3NoYXJrLWFjYWRlbXkiLCJraWQiOiIxMjM0NTY3ODc5IiwiYWxnIjoiSFMyNTYifQ.eyJjaWQiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJkYnMiOiJzYW5kYm94IiwiZXhwIjoxOTg0MzEwNjQzLCJpYXQiOjE2ODQzMDcwNDMsImlzcyI6Imh0dHA6Ly9kZXYuY3J5cHRyLmV1L3Qvc2hhcmstYWNhZGVteSIsImp0aSI6ImFhMTM3NDI5LTE1NDgtNDRmMC04ZTY4LTk3ZDAzYzFkMDBmNyIsImp0dCI6ImFwaV9rZXkiLCJzY3AiOiJyZWFkX21hbnk6c3NvX2Nvbm5lY3Rpb25zIHVwZGF0ZTpzc29fY29ubmVjdGlvbnMiLCJzdWIiOiJmZDNjOTFjYy1mODc0LTRiZTAtYjQxOS0xYjU5ODk2ODY4MjAiLCJ0bnQiOiJzaGFyay1hY2FkZW15IiwidmVyIjoxfQ.q20l-u-8gjsHDkW1IQUErVdgGykWrZmiGaojMMfrVD4"
        )
    }


    @Test
    fun listOrganizations() {
        stubFor(
            get("/api/v2/organizations")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
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
                    )
                )
        )
        val organizationResponse = cryptr.listOrganizations()
        assertNotNull(organizationResponse)
        if (organizationResponse is APISuccess) {
            val organizationListing = organizationResponse.value
            assertEquals(2, organizationListing.data.size)
            assertEquals(23, organizationListing.total)
            assertContains(
                organizationListing.data,
                Organization(
                    domain = "thibaud-paco",
                    name = "Thibaud Paco",
                    updatedAt = "2023-04-27T13:55:03",
                    insertedAt = "2023-04-27T13:54:42",
                    environments = setOf(
                        Environment(
                            name = "production",
                            status = EnvironmentStatus.DOWN
                        ),
                        Environment(
                            name = "sandbox",
                            status = EnvironmentStatus.DOWN
                        )
                    )
                )
            )
            assertContains(
                organizationListing.data,
                Organization(
                    domain = "thibaud-java",
                    name = "thibaud-java",
                    updatedAt = "2023-04-27T13:50:49",
                    insertedAt = "2023-04-27T13:50:29",
                    environments = setOf(
                        Environment(
                            name = "production",
                            status = EnvironmentStatus.DOWN
                        ),
                        Environment(
                            name = "sandbox",
                            status = EnvironmentStatus.DOWN
                        )
                    )
                )
            )
        }

    }

    @Test
    fun getOrganization() {
        stubFor(
            get("/api/v2/organizations/thibaud-java")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__type__\": \"Organization\",\n" +
                                "    \"domain\": \"thibaud-java\",\n" +
                                "    \"environments\": [\n" +
                                "        {\n" +
                                "            \"id\": \"7389e2d4-e49c-4371-8bfb-1f6bc243fe74\",\n" +
                                "            \"name\": \"production\",\n" +
                                "            \"status\": \"down\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"id\": \"f3751057-724d-41e8-9057-73067d46e715\",\n" +
                                "            \"name\": \"sandbox\",\n" +
                                "            \"status\": \"down\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"inserted_at\": \"2023-04-27T13:50:29\",\n" +
                                "    \"name\": \"thibaud-java\",\n" +
                                "    \"updated_at\": \"2023-04-27T13:50:49\"\n" +
                                "}"
                    )
                )

        )
        val resp = cryptr.retrieveOrganization("thibaud-java")
        if (resp is APISuccess) {
            assertEquals(
                Organization(
                    name = "thibaud-java",
                    domain = "thibaud-java",
                    updatedAt = "2023-04-27T13:50:49",
                    insertedAt = "2023-04-27T13:50:29",
                    environments = setOf(
                        Environment(
                            name = "production",
                            status = EnvironmentStatus.DOWN
                        ),
                        Environment(
                            name = "sandbox",
                            status = EnvironmentStatus.DOWN
                        )
                    )
                ), resp.value
            )
        }

    }

    @Test
    fun createOrganization() {
        stubFor(
            post("/api/v2/organizations")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__type__\": \"Organization\",\n" +
                                "    \"domain\": \"another-organization\",\n" +
                                "    \"environments\": [\n" +
                                "        {\n" +
                                "            \"id\": \"3aeaaa7d-9c9f-409b-b598-b08975673907\",\n" +
                                "            \"name\": \"production\",\n" +
                                "            \"status\": \"down\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"id\": \"410b9e25-95d8-4694-b0c1-4e3d29f490f5\",\n" +
                                "            \"name\": \"sandbox\",\n" +
                                "            \"status\": \"down\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"inserted_at\": \"2023-05-03T14:58:45\",\n" +
                                "    \"name\": \"Another organization\",\n" +
                                "    \"updated_at\": \"2023-05-03T14:58:45\"\n" +
                                "}"
                    )
                )
        )
        val org = Organization(name = "Another organization")
        val createdOrgaResp = cryptr.createOrganization(org)
        assertNotNull(createdOrgaResp)
        if (createdOrgaResp is APISuccess) {
            val createdOrga = createdOrgaResp.value
            assertEquals(org.name, createdOrga.name)

            val domain = Normalizer
                .normalize(org.name, Normalizer.Form.NFD)
                .replace("[^\\p{ASCII}]".toRegex(), "")
                .replace("[^a-zA-Z0-9\\s]+".toRegex(), "")
                .trim().replace("\\s+".toRegex(), "-")
                .lowercase()
            assertEquals(domain, createdOrga.domain)
        }
    }

    @Test
    fun deleteOrganization() {
        stubFor(
            delete("/api/v2/organizations/my-organization")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "  \"deleted\": true,\n" +
                                "  \"resource\": {\n" +
                                "    \"__type__\": \"Organization\",\n" +
                                "    \"domain\": \"thibaud-java\",\n" +
                                "    \"environments\": [\n" +
                                "      {\n" +
                                "        \"id\": \"7389e2d4-e49c-4371-8bfb-1f6bc243fe74\",\n" +
                                "        \"name\": \"production\",\n" +
                                "        \"status\": \"down\"\n" +
                                "      },\n" +
                                "      {\n" +
                                "        \"id\": \"f3751057-724d-41e8-9057-73067d46e715\",\n" +
                                "        \"name\": \"sandbox\",\n" +
                                "        \"status\": \"down\"\n" +
                                "      }\n" +
                                "    ],\n" +
                                "    \"inserted_at\": \"2023-04-27T13:50:29\",\n" +
                                "    \"name\": \"thibaud-java\",\n" +
                                "    \"updated_at\": \"2023-04-27T13:50:49\"\n" +
                                "  }\n" +
                                "}"
                    )
                )
        )

        val result = cryptr.deleteOrganization(Organization(name = "my Organization", domain = "my-organization"))
        assertIs<DeletedResource>(result)
        assertNotNull(result)
        assertTrue(result.deleted)
    }

    @Test
    fun listUsers() {
        stubFor(
            get("/api/v2/org/acme-company/users")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__type__\": \"List\",\n" +
                                "    \"data\": [\n" +
                                "        {\n" +
                                "            \"__domain__\": \"acme-company\",\n" +
                                "            \"__environment__\": \"sandbox\",\n" +
                                "            \"__type__\": \"User\",\n" +
                                "            \"address\": {\n" +
                                "                \"country\": \"FR\",\n" +
                                "                \"formatted\": \"165 avenue de Bretagne\\n59000, France\",\n" +
                                "                \"locality\": null,\n" +
                                "                \"postal_code\": \"59000\",\n" +
                                "                \"region\": \"Nord\",\n" +
                                "                \"street_address\": \"165 avenue de Bretagne\"\n" +
                                "            },\n" +
                                "            \"email\": \"nedra_boehm@hotmail.com\",\n" +
                                "            \"email_verified\": false,\n" +
                                "            \"id\": \"61254d31-3a33-4b10-bc22-f410f4927d42\",\n" +
                                "            \"inserted_at\": \"2023-05-02T12:09:41\",\n" +
                                "            \"meta_data\": [],\n" +
                                "            \"phone_number\": \"+1 555-415-1337\",\n" +
                                "            \"phone_number_verified\": false,\n" +
                                "            \"profile\": {\n" +
                                "                \"birthdate\": \"1943-01-19\",\n" +
                                "                \"family_name\": \"Joplin\",\n" +
                                "                \"gender\": \"female\",\n" +
                                "                \"given_name\": \"Janis\",\n" +
                                "                \"locale\": \"fr\",\n" +
                                "                \"nickname\": \"Jany\",\n" +
                                "                \"picture\": \"http://www.example.com/avatar.jpeg\",\n" +
                                "                \"preferred_username\": null,\n" +
                                "                \"website\": \"http://www.example.com\",\n" +
                                "                \"zoneinfo\": \"America/Los_Angeles\"\n" +
                                "            },\n" +
                                "            \"updated_at\": \"2023-05-02T12:09:41\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"__domain__\": \"acme-company\",\n" +
                                "            \"__environment__\": \"sandbox\",\n" +
                                "            \"__type__\": \"User\",\n" +
                                "            \"address\": null,\n" +
                                "            \"email\": \"omvold7jx62g@acme-company.io\",\n" +
                                "            \"email_verified\": false,\n" +
                                "            \"id\": \"d5f20c7c-c151-4177-8ee8-071d32317ea8\",\n" +
                                "            \"inserted_at\": \"2023-04-28T15:24:55\",\n" +
                                "            \"meta_data\": [],\n" +
                                "            \"phone_number\": null,\n" +
                                "            \"phone_number_verified\": false,\n" +
                                "            \"profile\": {\n" +
                                "                \"birthdate\": null,\n" +
                                "                \"family_name\": null,\n" +
                                "                \"gender\": null,\n" +
                                "                \"given_name\": null,\n" +
                                "                \"locale\": null,\n" +
                                "                \"nickname\": null,\n" +
                                "                \"picture\": null,\n" +
                                "                \"preferred_username\": null,\n" +
                                "                \"website\": null,\n" +
                                "                \"zoneinfo\": null\n" +
                                "            },\n" +
                                "            \"updated_at\": \"2023-04-28T15:24:55\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"pagination\": {\n" +
                                "        \"current_page\": 1,\n" +
                                "        \"current_pages\": [\n" +
                                "            1,\n" +
                                "            2,\n" +
                                "            3,\n" +
                                "            4,\n" +
                                "            5\n" +
                                "        ],\n" +
                                "        \"next_page\": 2,\n" +
                                "        \"per_page\": 2,\n" +
                                "        \"prev_page\": null,\n" +
                                "        \"total_pages\": 5\n" +
                                "    },\n" +
                                "    \"total\": 10\n" +
                                "}"
                    )
                )
        )
        val userListingResponse = cryptr.listUsers("acme-company")
        assertNotNull(userListingResponse)
        if (userListingResponse is APISuccess) {
            val userListing = userListingResponse.value
            assertIs<List<User>>(userListing)
            assertEquals(10, userListing.total)
            assertEquals(2, userListing.data.size)
            assertContains(userListing.data.map { u -> u.email }, "omvold7jx62g@acme-company.io")
            assertContains(userListing.data.map { u -> u.email }, "nedra_boehm@hotmail.com")
            assertContains(userListing.data.map { u -> u.address }, null)
        }
    }

    @Test
    fun getUserShouldReturnUser() {
        stubFor(
            get("/api/v2/org/acme-company/users/61254d31-3a33-4b10-bc22-f410f4927d42")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__type__\": \"User\",\n" +
                                "    \"address\": {\n" +
                                "        \"country\": \"FR\",\n" +
                                "        \"formatted\": \"165 avenue de Bretagne\\n59000, France\",\n" +
                                "        \"locality\": null,\n" +
                                "        \"postal_code\": \"59000\",\n" +
                                "        \"region\": \"Nord\",\n" +
                                "        \"street_address\": \"165 avenue de Bretagne\"\n" +
                                "    },\n" +
                                "    \"email\": \"nedra_boehm@hotmail.com\",\n" +
                                "    \"email_verified\": false,\n" +
                                "    \"id\": \"61254d31-3a33-4b10-bc22-f410f4927d42\",\n" +
                                "    \"inserted_at\": \"2023-05-02T12:09:41\",\n" +
                                "    \"meta_data\": [],\n" +
                                "    \"phone_number\": \"+1 555-415-1337\",\n" +
                                "    \"phone_number_verified\": false,\n" +
                                "    \"profile\": {\n" +
                                "        \"birthdate\": \"1943-01-19\",\n" +
                                "        \"family_name\": \"Joplin\",\n" +
                                "        \"gender\": \"female\",\n" +
                                "        \"given_name\": \"Janis\",\n" +
                                "        \"locale\": \"fr\",\n" +
                                "        \"nickname\": \"Jany\",\n" +
                                "        \"picture\": \"http://www.example.com/avatar.jpeg\",\n" +
                                "        \"preferred_username\": null,\n" +
                                "        \"website\": \"http://www.example.com\",\n" +
                                "        \"zoneinfo\": \"America/Los_Angeles\"\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-05-02T12:09:41\"\n" +
                                "}"
                    )
                )
        )

        val resp = cryptr.retrieveUser("acme-company", "61254d31-3a33-4b10-bc22-f410f4927d42")
        assertNotNull(resp)
        if (resp is APISuccess) {
            val user = resp.value
            assertEquals(user.email, "nedra_boehm@hotmail.com")
            assertEquals("FR", user.address?.country)
            assertEquals("165 avenue de Bretagne\n59000, France", user.address?.formatted)
            assertNull(user.address?.locality)
            assertEquals("59000", user.address?.postalCode)
            assertEquals("165 avenue de Bretagne", user.address?.streetAddress)

        }
    }

    @Test
    fun createUserByEmail() {
        stubFor(
            post("/api/v2/org/acme-company/users")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
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
                                "        \"given_name\": null,\n" +
                                "        \"locale\": null,\n" +
                                "        \"nickname\": null,\n" +
                                "        \"picture\": null,\n" +
                                "        \"preferred_username\": null,\n" +
                                "        \"website\": null,\n" +
                                "        \"zoneinfo\": null\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-05-03T14:03:09\"\n" +
                                "}"
                    )
                )
        )

        val resp = cryptr.createUser("acme-company", "aryanna.stroman@gmail.com")
        assertNotNull(resp)
        if (resp is APISuccess) {
            val user = resp.value
            assertEquals("aryanna.stroman@gmail.com", user.email)
            assertNull(user.profile?.birthdate)
            assertNull(user.address)
            assertNull(user.profile?.familyName)
            assertNull(user.profile?.gender)
            assertNull(user.profile?.givenName)
            assertNull(user.profile?.locale)
            assertNull(user.profile?.nickname)
            assertNull(user.phoneNumber)
            assertNull(user.profile?.picture)
            assertNull(user.profile?.website)
            assertNull(user.profile?.zoneInfo)
        }
    }

    @Test
    fun createUser() {
        stubFor(
            post("/api/v2/org/acme-company/users")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__type__\": \"User\",\n" +
                                "    \"address\": null,\n" +
                                "    \"email\": \"giuseppe.schoen@hotmail.com\",\n" +
                                "    \"email_verified\": false,\n" +
                                "    \"id\": \"d1e61734-514d-4755-8697-91143d11e528\",\n" +
                                "    \"inserted_at\": \"2023-05-03T14:10:52\",\n" +
                                "    \"meta_data\": [],\n" +
                                "    \"phone_number\": null,\n" +
                                "    \"phone_number_verified\": false,\n" +
                                "    \"profile\": {\n" +
                                "        \"birthdate\": null,\n" +
                                "        \"family_name\": null,\n" +
                                "        \"gender\": null,\n" +
                                "        \"given_name\": null,\n" +
                                "        \"locale\": null,\n" +
                                "        \"nickname\": null,\n" +
                                "        \"picture\": null,\n" +
                                "        \"preferred_username\": null,\n" +
                                "        \"website\": null,\n" +
                                "        \"zoneinfo\": null\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-05-03T14:10:52\"\n" +
                                "}"
                    )
                )
        )

        val user = User(email = "omvold7jx62g@acme-company.io")
        val resp = cryptr.createUser("acme-company", user)
        assertNotNull(resp)
        if (resp is APISuccess) {
            assertNull(resp.value.address?.postalCode)
        }
    }

    @Test
    fun updateUser() {
        stubFor(
            put("/api/v2/org/acme-company/users/d1e61734-514d-4755-8697-91143d11e528")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__type__\": \"User\",\n" +
                                "    \"address\": null,\n" +
                                "    \"email\": \"giuseppe.schoen@hotmail.com\",\n" +
                                "    \"email_verified\": false,\n" +
                                "    \"id\": \"d1e61734-514d-4755-8697-91143d11e528\",\n" +
                                "    \"inserted_at\": \"2023-05-03T14:10:52\",\n" +
                                "    \"meta_data\": [],\n" +
                                "    \"phone_number\": null,\n" +
                                "    \"phone_number_verified\": false,\n" +
                                "    \"profile\": {\n" +
                                "        \"birthdate\": null,\n" +
                                "        \"family_name\": \"SCHOEN\",\n" +
                                "        \"gender\": null,\n" +
                                "        \"given_name\": \"Giuseppe\",\n" +
                                "        \"locale\": null,\n" +
                                "        \"nickname\": null,\n" +
                                "        \"picture\": null,\n" +
                                "        \"preferred_username\": null,\n" +
                                "        \"website\": null,\n" +
                                "        \"zoneinfo\": null\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-05-03T14:10:52\"\n" +
                                "}"
                    )
                )
        )

        val response = cryptr.updateUser(
            User(
                resourceDomain = "acme-company",
                id = "d1e61734-514d-4755-8697-91143d11e528",
                email = "giuseppe.schoen@hotmail.com",
                profile = Profile(
                    familyName = "SCHOEN",
                    givenName = "Giuseppe"
                )
            )
        )

        assertNotNull(response)
        if (response is APISuccess) {
            assertIs<User>(response.value)
            assertEquals("SCHOEN", response.value.profile?.familyName)
            assertEquals("Giuseppe", response.value.profile?.givenName)
        }
    }

    @Test
    fun deleeteUser() {
        stubFor(
            delete("/api/v2/org/acme-company/users/d1e61734-514d-4755-8697-91143d11e528")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "  \"deleted\": true,\n" +
                                "  \"resource\": {\n" +
                                "  \"__domain__\": \"acme-company\",\n" +
                                "  \"__environment__\": \"sandbox\",\n" +
                                "  \"__type__\": \"User\",\n" +
                                "  \"address\": null,\n" +
                                "  \"email\": \"giuseppe.schoen@hotmail.com\",\n" +
                                "  \"email_verified\": false,\n" +
                                "  \"id\": \"d1e61734-514d-4755-8697-91143d11e528\",\n" +
                                "  \"inserted_at\": \"2023-05-03T14:10:52\",\n" +
                                "  \"meta_data\": [],\n" +
                                "  \"phone_number\": null,\n" +
                                "  \"phone_number_verified\": false,\n" +
                                "  \"profile\": {\n" +
                                "    \"birthdate\": null,\n" +
                                "    \"family_name\": \"SCHOEN\",\n" +
                                "    \"gender\": null,\n" +
                                "    \"given_name\": \"Giuseppe\",\n" +
                                "    \"locale\": null,\n" +
                                "    \"nickname\": null,\n" +
                                "    \"picture\": null,\n" +
                                "    \"preferred_username\": null,\n" +
                                "    \"website\": null,\n" +
                                "    \"zoneinfo\": null\n" +
                                "  },\n" +
                                "  \"updated_at\": \"2023-05-03T14:10:52\"\n" +
                                "}\n" +
                                "}"
                    )
                )
        )

        val response = cryptr.deleteUser(
            User(
                resourceDomain = "acme-company",
                id = "d1e61734-514d-4755-8697-91143d11e528",
                email = "giuseppe.schoen@hotmail.com"
            )
        )
        assertIs<DeletedUser>(response)
        assertTrue(response.deleted)
        assertEquals("giuseppe.schoen@hotmail.com", response.resource.email)
        assertEquals("acme-company", response.resource.resourceDomain)
    }

    @Test
    fun deleteUserShouldThrowError() {
        stubFor(
            delete("/api/v2/org/acme-company/users/d1e61734-514d-4755-869-91143d11e528")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(notFound())
        )

        val response = cryptr.deleteUser(
            User(
                resourceDomain = "acme-company",
                id = "d1e61734-514d-4755-869-91143d11e528",
                email = "giuseppe.schoen@hotmail.com"
            )
        )
        assertNull(response)
    }

    @Test
    fun successfulCreatePasswordConnection() {
        stubFor(
            post("/api/v2/org/acme-company/password-connection")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "  \"__domain__\": \"acme-company\",\n" +
                                "  \"__type__\": \"PasswordConnection\",\n" +
                                "  \"id\": \"b6f3da01-b571-48c3-a630-6f1fddf5af31\",\n" +
                                "  \"inserted_at\": \"2023-06-08T13:47:41\",\n" +
                                "  \"pepper_rotation_period\": 86400,\n" +
                                "  \"plain_text_max_length\": 40,\n" +
                                "  \"plain_text_min_length\": 8,\n" +
                                "  \"updated_at\": \"2023-06-08T13:47:41\"\n" +
                                "}"
                    )
                )
        )

        val passwordConnectionResponse = cryptr.createPasswordConnection("acme-company")
        assertNotNull(passwordConnectionResponse)
        if (passwordConnectionResponse is APISuccess) {
            val createdPasswordConnection = passwordConnectionResponse.value
            assertIs<PasswordConnection>(createdPasswordConnection)
            assertEquals("PasswordConnection", createdPasswordConnection.cryptrType)
            assertNotNull(createdPasswordConnection.id)
            assertEquals("acme-company", createdPasswordConnection.resourceDomain)
            assertTrue { createdPasswordConnection.plainTextMinLength!! > 0 }
            assertTrue { createdPasswordConnection.plainTextMaxLength!! >= createdPasswordConnection.plainTextMinLength!! }
            assertEquals(40, createdPasswordConnection.plainTextMaxLength)
            assertEquals(8, createdPasswordConnection.plainTextMinLength)
        }
    }

    @Test
    fun failedCreatePasswordConnection() {
        stubFor(
            post("/api/v2/org/acme-company/password-connection")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok("{\"error\":\"internal_server_error\",\"error_description\":\"incompatible_connection\"}")
                )
        )

        val passwordConnectionResponse = cryptr.createPasswordConnection("acme-company")
        assertNotNull(passwordConnectionResponse)

        assertFalse { passwordConnectionResponse is APISuccess }
        assertTrue { passwordConnectionResponse is APIError }
    }
}