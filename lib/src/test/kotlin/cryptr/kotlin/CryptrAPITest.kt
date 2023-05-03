package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.enums.ApplicationType
import cryptr.kotlin.models.Application
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.Profile
import cryptr.kotlin.models.User
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.Normalizer
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull

@WireMockTest(proxyMode = true)
class CryptrAPITest {
    var cryptrApi: CryptrAPI? = null

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        val defaultRedirectUrl = "http://localhost:8080/callback"
        val apiKeyClientId = "my-api-key-client-id"
        val apiKeyClientSecret = "my-api-key-client-secret"
        cryptrApi = CryptrAPI(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret)
        System.setProperty("CRYPTR_API_KEY_TOKEN", "stored-api-key")
    }


    @Test
    fun listOrganizations() {
        stubFor(
            get("/api/v2/organizations")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__type__\": \"List\",\n" +
                                "    \"data\": [\n" +
                                "        {\n" +
                                "            \"__access__\": \"limited_to:test-store\",\n" +
                                "            \"__domain__\": \"test-store\",\n" +
                                "            \"__environment__\": \"production\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"Organization\",\n" +
                                "            \"country_name\": \"FR\",\n" +
                                "            \"domain\": \"test-store\",\n" +
                                "            \"id\": \"6b99d8fc-28be-45d5-8571-4206af5ff376\",\n" +
                                "            \"inserted_at\": \"2023-01-10T11:13:08\",\n" +
                                "            \"locality\": \"Lille\",\n" +
                                "            \"name\": \"Test Store\",\n" +
                                "            \"privacy_policy_url\": null,\n" +
                                "            \"state\": \"Nord\",\n" +
                                "            \"terms_of_service_url\": null,\n" +
                                "            \"updated_at\": \"2023-01-10T11:13:13\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"__access__\": \"limited_to:toffee-company\",\n" +
                                "            \"__domain__\": \"toffee-company\",\n" +
                                "            \"__environment__\": \"production\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"Organization\",\n" +
                                "            \"country_name\": \"FR\",\n" +
                                "            \"domain\": \"toffee-company\",\n" +
                                "            \"id\": \"10876fdb-2ee8-41c7-899e-97ad27d7cd0f\",\n" +
                                "            \"inserted_at\": \"2023-01-04T10:00:07\",\n" +
                                "            \"locality\": \"Lille\",\n" +
                                "            \"name\": \"Toffee company\",\n" +
                                "            \"privacy_policy_url\": null,\n" +
                                "            \"state\": \"Nord\",\n" +
                                "            \"terms_of_service_url\": null,\n" +
                                "            \"updated_at\": \"2023-01-04T10:00:20\"\n" +
                                "        },\n" +
                                "    ],\n" +
                                "    \"paginate\": {\n" +
                                "        \"current_page\": 1,\n" +
                                "        \"next_page\": 2,\n" +
                                "        \"per_page\": 8,\n" +
                                "        \"prev_page\": null,\n" +
                                "        \"total_pages\": 3\n" +
                                "    },\n" +
                                "    \"total_count\": 23\n" +
                                "}"
                    )
                )
        )
        val resp = cryptrApi?.listOrganizations()
        assertEquals(2, resp?.size)

        if (resp != null) {
            assertContains(
                resp.toArray(),
                Organization(
                    id = "6b99d8fc-28be-45d5-8571-4206af5ff376",
                    domain = "test-store",
                    name = "Test Store",
                    updatedAt = "2023-01-10T11:13:13",
                    state = "Nord",
                    countryName = "FR",
                    locality = "Lille",
                    insertedAt = "2023-01-10T11:13:08"
                )
            )
            assertContains(
                resp.toArray(),
                Organization(
                    id = "10876fdb-2ee8-41c7-899e-97ad27d7cd0f",
                    domain = "toffee-company",
                    name = "Toffee company",
                    updatedAt = "2023-01-04T10:00:20",
                    state = "Nord",
                    countryName = "FR",
                    locality = "Lille",
                    insertedAt = "2023-01-04T10:00:07"
                )
            )
        }
    }

    @Test
    fun getOrganization() {
        stubFor(
            get("/api/v2/organizations/toffee-company")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__access__\": \"limited_to:toffee-company\",\n" +
                                "    \"__domain__\": \"toffee-company\",\n" +
                                "    \"__environment__\": \"production\",\n" +
                                "    \"__managed_by__\": \"shark-academy\",\n" +
                                "    \"__type__\": \"Organization\",\n" +
                                "    \"country_name\": \"FR\",\n" +
                                "    \"domain\": \"toffee-company\",\n" +
                                "    \"id\": \"10876fdb-2ee8-41c7-899e-97ad27d7cd0f\",\n" +
                                "    \"inserted_at\": \"2023-01-04T10:00:07\",\n" +
                                "    \"locality\": \"Lille\",\n" +
                                "    \"name\": \"Toffee company\",\n" +
                                "    \"privacy_policy_url\": null,\n" +
                                "    \"state\": \"Nord\",\n" +
                                "    \"terms_of_service_url\": null,\n" +
                                "    \"updated_at\": \"2023-01-04T10:00:20\"\n" +
                                "}"
                    )
                )

        )
        val resp = cryptrApi?.getOrganization("toffee-company")
        assertNotNull(resp)
        assertEquals(
            Organization(
                id = "10876fdb-2ee8-41c7-899e-97ad27d7cd0f",
                name = "Toffee company",
                domain = "toffee-company",
                countryName = "FR",
                state = "Nord",
                locality = "Lille",
                updatedAt = "2023-01-04T10:00:20",
                insertedAt = "2023-01-04T10:00:07"
            ), resp
        )
    }

    @Test
    fun createOrganization() {
        stubFor(
            post("/api/v2/organizations")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"id\": \"some-api-key-id\",\n" +
                                "    \"domain\": \"thibaud-paco\",\n" +
                                "    \"name\": \"Thibaud Paco\",\n" +
                                "    \"country_name\": \"FR\",\n" +
                                "    \"state\": \"Hauts-de-France\",\n" +
                                "    \"locality\": \"Lille\",\n" +
                                "    \"updated_at\": \"2023-01-04T10:00:20\",\n" +
                                "    \"inserted_at\": \"2023-01-04T10:00:20\"\n" +
                                "}"
                    )
                )
        )
        val org = Organization(name = "Thibaud Paco")
        val createdOrga = cryptrApi?.createOrganization(org)
        assertNotNull(createdOrga)
        if (createdOrga != null) {
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
                                "            \"__access__\": \"limited_to:acme-company\",\n" +
                                "            \"__domain__\": \"acme-company\",\n" +
                                "            \"__environment__\": \"sandbox\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"User\",\n" +
                                "            \"id\": \"d5f20c7c-c151-4177-8ee8-071d32317ea8\",\n" +
                                "            \"inserted_at\": \"2023-04-28T15:24:55\",\n" +
                                "            \"metadata\": [],\n" +
                                "            \"profile\": {\n" +
                                "                \"__access__\": \"limited_to:acme-company\",\n" +
                                "                \"__domain__\": \"acme-company\",\n" +
                                "                \"__environment__\": \"sandbox\",\n" +
                                "                \"__managed_by__\": \"shark-academy\",\n" +
                                "                \"__type__\": \"Profile\",\n" +
                                "                \"address\": null,\n" +
                                "                \"birthdate\": null,\n" +
                                "                \"email\": \"omvold7jx62g@acme-company.io\",\n" +
                                "                \"family_name\": null,\n" +
                                "                \"gender\": null,\n" +
                                "                \"given_name\": null,\n" +
                                "                \"locale\": null,\n" +
                                "                \"name\": \" \",\n" +
                                "                \"nickname\": null,\n" +
                                "                \"phone_number\": null,\n" +
                                "                \"picture\": null,\n" +
                                "                \"profile\": null,\n" +
                                "                \"website\": null,\n" +
                                "                \"zoneinfo\": null\n" +
                                "            },\n" +
                                "            \"updated_at\": \"2023-04-28T15:24:55\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"__access__\": \"limited_to:acme-company\",\n" +
                                "            \"__domain__\": \"acme-company\",\n" +
                                "            \"__environment__\": \"sandbox\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"User\",\n" +
                                "            \"id\": \"3279619a-f826-4f86-bd86-6a1a8ae719d4\",\n" +
                                "            \"inserted_at\": \"2023-04-28T15:21:44\",\n" +
                                "            \"metadata\": [],\n" +
                                "            \"profile\": {\n" +
                                "                \"__access__\": \"limited_to:acme-company\",\n" +
                                "                \"__domain__\": \"acme-company\",\n" +
                                "                \"__environment__\": \"sandbox\",\n" +
                                "                \"__managed_by__\": \"shark-academy\",\n" +
                                "                \"__type__\": \"Profile\",\n" +
                                "                \"address\": null,\n" +
                                "                \"birthdate\": null,\n" +
                                "                \"email\": \"12345@acme-company.co\",\n" +
                                "                \"family_name\": null,\n" +
                                "                \"gender\": null,\n" +
                                "                \"given_name\": null,\n" +
                                "                \"locale\": null,\n" +
                                "                \"name\": \" \",\n" +
                                "                \"nickname\": null,\n" +
                                "                \"phone_number\": null,\n" +
                                "                \"picture\": null,\n" +
                                "                \"profile\": null,\n" +
                                "                \"website\": null,\n" +
                                "                \"zoneinfo\": null\n" +
                                "            },\n" +
                                "            \"updated_at\": \"2023-04-28T15:21:44\"\n" +
                                "        },\n" +
                                "    ],\n" +
                                "    \"paginate\": {\n" +
                                "        \"current_page\": 1,\n" +
                                "        \"next_page\": 2,\n" +
                                "        \"per_page\": 8,\n" +
                                "        \"prev_page\": null,\n" +
                                "        \"total_pages\": 2\n" +
                                "    },\n" +
                                "    \"total_count\": 9\n" +
                                "}"
                    )
                )
        )
        val resp = cryptrApi?.listUsers("acme-company")
        assertEquals(2, resp?.size)

        if (resp !== null) {
            assertContains(resp.map { u -> u.profile.email }, "omvold7jx62g@acme-company.io")
            assertContains(resp.map { u -> u.profile.email }, "12345@acme-company.co")
            assertContains(resp.map { u -> u.profile.address }, null)
        }
    }

    @Test
    fun getUserShouldReturnUser() {
        stubFor(
            get("/api/v2/org/acme-company/users/d5f20c7c-c151-4177-8ee8-071d32317ea8")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__access__\": \"limited_to:acme-company\",\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__managed_by__\": \"shark-academy\",\n" +
                                "    \"__type__\": \"User\",\n" +
                                "    \"id\": \"d5f20c7c-c151-4177-8ee8-071d32317ea8\",\n" +
                                "    \"inserted_at\": \"2023-04-28T15:24:55\",\n" +
                                "    \"metadata\": [],\n" +
                                "    \"profile\": {\n" +
                                "        \"__access__\": \"limited_to:acme-company\",\n" +
                                "        \"__domain__\": \"acme-company\",\n" +
                                "        \"__environment__\": \"sandbox\",\n" +
                                "        \"__managed_by__\": \"shark-academy\",\n" +
                                "        \"__type__\": \"Profile\",\n" +
                                "        \"address\": null,\n" +
                                "        \"birthdate\": null,\n" +
                                "        \"email\": \"omvold7jx62g@acme-company.io\",\n" +
                                "        \"family_name\": null,\n" +
                                "        \"gender\": null,\n" +
                                "        \"given_name\": null,\n" +
                                "        \"locale\": null,\n" +
                                "        \"name\": \" \",\n" +
                                "        \"nickname\": null,\n" +
                                "        \"phone_number\": null,\n" +
                                "        \"picture\": null,\n" +
                                "        \"profile\": null,\n" +
                                "        \"website\": null,\n" +
                                "        \"zoneinfo\": null\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-04-28T15:24:55\"\n" +
                                "}"
                    )
                )
        )

        val resp = cryptrApi?.getUser("acme-company", "d5f20c7c-c151-4177-8ee8-071d32317ea8")
        assertNotNull(resp)
        if (resp != null) {
            assertEquals(resp.profile.email, "omvold7jx62g@acme-company.io")
            assertNull(resp.profile.address)
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
                                "    \"__access__\": \"limited_to:acme-company\",\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__managed_by__\": \"shark-academy\",\n" +
                                "    \"__type__\": \"User\",\n" +
                                "    \"id\": \"61254d31-3a33-4b10-bc22-f410f4927d42\",\n" +
                                "    \"inserted_at\": \"2023-05-02T12:09:41\",\n" +
                                "    \"metadata\": [],\n" +
                                "    \"profile\": {\n" +
                                "        \"__access__\": \"limited_to:acme-company\",\n" +
                                "        \"__domain__\": \"acme-company\",\n" +
                                "        \"__environment__\": \"sandbox\",\n" +
                                "        \"__managed_by__\": \"shark-academy\",\n" +
                                "        \"__type__\": \"Profile\",\n" +
                                "        \"address\": {\n" +
                                "            \"__access__\": \"limited_to:acme-company\",\n" +
                                "            \"__domain__\": \"acme-company\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"Address\",\n" +
                                "            \"country\": \"FR\",\n" +
                                "            \"formatted\": \"165 avenue de Bretagne\\n59000, France\",\n" +
                                "            \"id\": \"d5095077-4d7c-4379-9924-cac9d13bced9\",\n" +
                                "            \"postal_code\": \"59000\",\n" +
                                "            \"region\": \"Nord\",\n" +
                                "            \"street_address\": \"165 avenue de Bretagne\"\n" +
                                "        },\n" +
                                "        \"birthdate\": \"1943-01-19\",\n" +
                                "        \"email\": \"nedra_boehm@hotmail.com\",\n" +
                                "        \"family_name\": \"Joplin\",\n" +
                                "        \"gender\": \"female\",\n" +
                                "        \"given_name\": \"Janis\",\n" +
                                "        \"locale\": \"fr\",\n" +
                                "        \"name\": \"Janis Joplin\",\n" +
                                "        \"nickname\": \"Jany\",\n" +
                                "        \"phone_number\": \"+1 555-415-1337\",\n" +
                                "        \"picture\": \"http://www.example.com/avatar.jpeg\",\n" +
                                "        \"profile\": \"http://www.example.com/profile\",\n" +
                                "        \"website\": \"http://www.example.com\",\n" +
                                "        \"zoneinfo\": \"America/Los_Angeles\"\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-05-02T12:09:41\"\n" +
                                "}"
                    )
                )
        )

        val resp = cryptrApi?.createUser("acme-company", "nedra_boehm@hotmail.com")
        assertNotNull(resp)
        if (resp != null) {
            assertEquals("nedra_boehm@hotmail.com", resp.profile.email)
            assertEquals("1943-01-19", resp.profile.birthdate)
            assertEquals("Joplin", resp.profile.familyName)
            assertEquals("female", resp.profile.gender)
            assertEquals("Janis", resp.profile.givenName)
            assertEquals("fr", resp.profile.locale)
            assertEquals("Janis Joplin", resp.profile.name)
            assertEquals("Jany", resp.profile.nickname)
            assertEquals("+1 555-415-1337", resp.profile.phoneNumber)
            assertEquals("http://www.example.com/avatar.jpeg", resp.profile.picture)
            assertEquals("http://www.example.com/profile", resp.profile.profile)
            assertEquals("http://www.example.com", resp.profile.website)
            assertEquals("America/Los_Angeles", resp.profile.zoneinfo)

            assertEquals("d5095077-4d7c-4379-9924-cac9d13bced9", resp.profile.address?.id)
            assertEquals("FR", resp.profile.address?.country)
            assertEquals("165 avenue de Bretagne", resp.profile.address?.streetAddress)
            assertEquals("165 avenue de Bretagne\n59000, France", resp.profile.address?.formatted)
            assertEquals("59000", resp.profile.address?.postalCode)
            assertEquals("Nord", resp.profile.address?.region)
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
                                "    \"__access__\": \"limited_to:acme-company\",\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__managed_by__\": \"shark-academy\",\n" +
                                "    \"__type__\": \"User\",\n" +
                                "    \"id\": \"61254d31-3a33-4b10-bc22-f410f4927d42\",\n" +
                                "    \"inserted_at\": \"2023-05-02T12:09:41\",\n" +
                                "    \"metadata\": [],\n" +
                                "    \"profile\": {\n" +
                                "        \"__access__\": \"limited_to:acme-company\",\n" +
                                "        \"__domain__\": \"acme-company\",\n" +
                                "        \"__environment__\": \"sandbox\",\n" +
                                "        \"__managed_by__\": \"shark-academy\",\n" +
                                "        \"__type__\": \"Profile\",\n" +
                                "        \"address\": {\n" +
                                "            \"__access__\": \"limited_to:acme-company\",\n" +
                                "            \"__domain__\": \"acme-company\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"Address\",\n" +
                                "            \"country\": \"FR\",\n" +
                                "            \"formatted\": \"165 avenue de Bretagne\\n59000, France\",\n" +
                                "            \"id\": \"d5095077-4d7c-4379-9924-cac9d13bced9\",\n" +
                                "            \"postal_code\": \"59000\",\n" +
                                "            \"region\": \"Nord\",\n" +
                                "            \"street_address\": \"165 avenue de Bretagne\"\n" +
                                "        },\n" +
                                "        \"birthdate\": \"1943-01-19\",\n" +
                                "        \"email\": \"nedra_boehm@hotmail.com\",\n" +
                                "        \"family_name\": \"Joplin\",\n" +
                                "        \"gender\": \"female\",\n" +
                                "        \"given_name\": \"Janis\",\n" +
                                "        \"locale\": \"fr\",\n" +
                                "        \"name\": \"Janis Joplin\",\n" +
                                "        \"nickname\": \"Jany\",\n" +
                                "        \"phone_number\": \"+1 555-415-1337\",\n" +
                                "        \"picture\": \"http://www.example.com/avatar.jpeg\",\n" +
                                "        \"profile\": \"http://www.example.com/profile\",\n" +
                                "        \"website\": \"http://www.example.com\",\n" +
                                "        \"zoneinfo\": \"America/Los_Angeles\"\n" +
                                "    },\n" +
                                "    \"updated_at\": \"2023-05-02T12:09:41\"\n" +
                                "}"
                    )
                )
        )

        val user = User(profile = Profile(email = "omvold7jx62g@acme-company.io"))
        val resp = cryptrApi?.createUser("acme-company", user)
        assertNotNull(resp)
        if (resp !== null) {
            assertEquals("59000", resp.profile.address?.postalCode)
        }
    }

    @Test
    fun listApplications() {
        stubFor(
            get("/api/v2/org/acme-company/applications")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__type__\": \"List\",\n" +
                                "    \"data\": [],\n" +
                                "    \"paginate\": {\n" +
                                "        \"current_page\": 1,\n" +
                                "        \"next_page\": null,\n" +
                                "        \"per_page\": 8,\n" +
                                "        \"prev_page\": null,\n" +
                                "        \"total_pages\": 0\n" +
                                "    },\n" +
                                "    \"total_count\": 0\n" +
                                "}"
                    )
                )
        )
        stubFor(
            get("/api/v2/org/shark-academy/applications")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__type__\": \"List\",\n" +
                                "    \"data\": [\n" +
                                "        {\n" +
                                "            \"__access__\": \"all_organizations_of:shark-academy\",\n" +
                                "            \"__domain__\": \"shark-academy\",\n" +
                                "            \"__environment__\": \"sandbox\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"Application\",\n" +
                                "            \"allowed_logout_urls\": [\n" +
                                "                \"https://debug-communitiz-app.onrender.com\"\n" +
                                "            ],\n" +
                                "            \"allowed_origins_cors\": [\n" +
                                "                \"https://debug-communitiz-app.onrender.com\"\n" +
                                "            ],\n" +
                                "            \"allowed_redirect_urls\": [\n" +
                                "                \"https://debug-communitiz-app.onrender.com\"\n" +
                                "            ],\n" +
                                "            \"application_type\": \"vue\",\n" +
                                "            \"client_id\": \"ae04b642-4214-40fa-bd6e-8ae35724c071\",\n" +
                                "            \"default_origin_cors\": \"https://debug-communitiz-app.onrender.com\",\n" +
                                "            \"default_redirect_uri_after_login\": \"https://debug-communitiz-app.onrender.com\",\n" +
                                "            \"default_redirect_uri_after_logout\": \"https://debug-communitiz-app.onrender.com\",\n" +
                                "            \"description\": null,\n" +
                                "            \"id\": \"ae04b642-4214-40fa-bd6e-8ae35724c071\",\n" +
                                "            \"inserted_at\": \"2023-02-24T10:27:22\",\n" +
                                "            \"name\": \"Community App Thibs debug App\",\n" +
                                "            \"updated_at\": \"2023-02-24T10:27:22\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"__access__\": \"all_organizations_of:shark-academy\",\n" +
                                "            \"__domain__\": \"shark-academy\",\n" +
                                "            \"__environment__\": \"sandbox\",\n" +
                                "            \"__managed_by__\": \"shark-academy\",\n" +
                                "            \"__type__\": \"Application\",\n" +
                                "            \"allowed_logout_urls\": [\n" +
                                "                \"https://communitiz-app-vuejs.onrender.com\"\n" +
                                "            ],\n" +
                                "            \"allowed_origins_cors\": [\n" +
                                "                \"https://communitiz-app-vuejs.onrender.com\"\n" +
                                "            ],\n" +
                                "            \"allowed_redirect_urls\": [\n" +
                                "                \"https://communitiz-app-vuejs.onrender.com\"\n" +
                                "            ],\n" +
                                "            \"application_type\": \"react\",\n" +
                                "            \"client_id\": \"a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f\",\n" +
                                "            \"default_origin_cors\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                                "            \"default_redirect_uri_after_login\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                                "            \"default_redirect_uri_after_logout\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                                "            \"description\": null,\n" +
                                "            \"id\": \"a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f\",\n" +
                                "            \"inserted_at\": \"2023-02-08T07:57:00\",\n" +
                                "            \"name\": \"Community App Communitiz Real QA App\",\n" +
                                "            \"updated_at\": \"2023-02-08T07:57:00\"\n" +
                                "        },\n" +
                                "    ],\n" +
                                "    \"paginate\": {\n" +
                                "        \"current_page\": 1,\n" +
                                "        \"next_page\": null,\n" +
                                "        \"per_page\": 8,\n" +
                                "        \"prev_page\": null,\n" +
                                "        \"total_pages\": 1\n" +
                                "    },\n" +
                                "    \"total_count\": 8\n" +
                                "}"
                    )
                )
        )

        val acmeApps = cryptrApi?.listApplications("acme-company")
        assertNotNull(acmeApps)
        assertEquals(0, acmeApps?.size)

        val apps = cryptrApi?.listApplications("shark-academy")
        assertNotNull(apps)
        if (apps !== null) {
            assertEquals(2, apps.size)
            assertContains(apps.map { a -> a.applicationType }, ApplicationType.REACT)
        }
    }

    @Test
    fun getApplication() {
        stubFor(
            get("/api/v2/org/shark-academy/applications/a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__access__\": \"all_organizations_of:shark-academy\",\n" +
                                "    \"__domain__\": \"shark-academy\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__managed_by__\": \"shark-academy\",\n" +
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
                                "    \"application_type\": \"react\",\n" +
                                "    \"client_id\": \"a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f\",\n" +
                                "    \"default_origin_cors\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                                "    \"default_redirect_uri_after_login\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                                "    \"default_redirect_uri_after_logout\": \"https://communitiz-app-vuejs.onrender.com\",\n" +
                                "    \"description\": null,\n" +
                                "    \"id\": \"a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f\",\n" +
                                "    \"inserted_at\": \"2023-02-08T07:57:00\",\n" +
                                "    \"name\": \"Community App Communitiz Real QA App\",\n" +
                                "    \"updated_at\": \"2023-02-08T07:57:00\"\n" +
                                "}"
                    )
                )
        )

        val app = cryptrApi?.getApplication("shark-academy", "a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f")
        assertNotNull(app)
        if (app !== null) {
            assertEquals("a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f", app.id)
            app.allowedLogoutUrls?.let { assertContains(it.asIterable(), "https://communitiz-app-vuejs.onrender.com") }
            app.allowedOriginsCors?.let { assertContains(it.asIterable(), "https://communitiz-app-vuejs.onrender.com") }
            app.allowedRedirectUrls?.let {
                assertContains(
                    it.asIterable(),
                    "https://communitiz-app-vuejs.onrender.com"
                )
            }
            assertEquals(ApplicationType.REACT, app.applicationType)
            assertEquals("a7c56b66-d7dc-46f9-a4a8-b93ef7e2120f", app.clientId)
            assertEquals("https://communitiz-app-vuejs.onrender.com", app.defaultOriginCors)
            assertEquals("https://communitiz-app-vuejs.onrender.com", app.defaultRedirectUriAfterLogin)
            assertEquals("https://communitiz-app-vuejs.onrender.com", app.defaultRedirectUriAfterLogout)
            assertNull(app.description)
            assertEquals("2023-02-08T07:57:00", app.insertedAt)
            assertEquals("Community App Communitiz Real QA App", app.name)
            assertEquals("2023-02-08T07:57:00", app.updatedAt)
        }
    }

    @Test
    fun createApplication() {
        stubFor(
            post("/api/v2/org/acme-company/applications")
                .withHost(equalTo("dev.cryptr.eu"))
                .willReturn(
                    ok(
                        "{\n" +
                                "    \"__access__\": \"limited_to:acme-company\",\n" +
                                "    \"__domain__\": \"acme-company\",\n" +
                                "    \"__environment__\": \"sandbox\",\n" +
                                "    \"__managed_by__\": \"shark-academy\",\n" +
                                "    \"__type__\": \"Application\",\n" +
                                "    \"allowed_logout_urls\": [\n" +
                                "        \"https://some-saas.io\"\n" +
                                "    ],\n" +
                                "    \"allowed_origins_cors\": [\n" +
                                "        \"https://some-saas.io\"\n" +
                                "    ],\n" +
                                "    \"allowed_redirect_urls\": [\n" +
                                "        \"https://some-saas.io\"\n" +
                                "    ],\n" +
                                "    \"application_type\": \"angular\",\n" +
                                "    \"client_id\": \"bc3583eb-59e3-4edf-83c4-96bd308430cc\",\n" +
                                "    \"default_origin_cors\": \"https://some-saas.io\",\n" +
                                "    \"default_redirect_uri_after_login\": \"https://some-saas.io\",\n" +
                                "    \"default_redirect_uri_after_logout\": \"https://some-saas.io\",\n" +
                                "    \"description\": null,\n" +
                                "    \"id\": \"bc3583eb-59e3-4edf-83c4-96bd308430cc\",\n" +
                                "    \"inserted_at\": \"2023-05-02T16:06:47\",\n" +
                                "    \"name\": \"Angular app\",\n" +
                                "    \"updated_at\": \"2023-05-02T16:06:47\"\n" +
                                "}"
                    )
                )
        )
        val app = cryptrApi?.createApplication(
            "acme-company",
            Application(name = "Angular App", applicationType = ApplicationType.ANGULAR)
        )
        assertNotNull(app)
        if (app !== null) {
            assertEquals("bc3583eb-59e3-4edf-83c4-96bd308430cc", app.id)
            app.allowedLogoutUrls?.let { assertContains(it.asIterable(), "https://some-saas.io") }
            app.allowedOriginsCors?.let { assertContains(it.asIterable(), "https://some-saas.io") }
            app.allowedRedirectUrls?.let { assertContains(it.asIterable(), "https://some-saas.io") }
            assertEquals(ApplicationType.ANGULAR, app.applicationType)
            assertEquals("bc3583eb-59e3-4edf-83c4-96bd308430cc", app.clientId)
            assertEquals("https://some-saas.io", app.defaultOriginCors)
            assertEquals("https://some-saas.io", app.defaultRedirectUriAfterLogin)
            assertEquals("https://some-saas.io", app.defaultRedirectUriAfterLogout)
            assertNull(app.description)
            assertEquals("2023-05-02T16:06:47", app.insertedAt)
            assertEquals("Angular app", app.name)
            assertEquals("2023-05-02T16:06:47", app.updatedAt)
        }
    }
}