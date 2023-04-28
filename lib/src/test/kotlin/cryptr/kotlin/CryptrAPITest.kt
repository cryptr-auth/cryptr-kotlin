package cryptr.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import cryptr.kotlin.models.Organization
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@WireMockTest(proxyMode = true)
class CryptrAPITest {
    var cryptrApi: CryptrAPI? = null

    @BeforeEach
    fun init() {
        val tenantDomain = "shark-academy"
        val baseUrl = "http://dev.cryptr.eu"
        //val baseUrl = "https://cleeck-umbrella-develop.onrender.com"
        val defaultRedirectUrl = "http://localhost:8080/callback"
        val apiKeyClientId = "62847327-2101-4a36-a51c-e7016098ee18"
        val apiKeyClientSecret = "qO0vCgXyUk7OjCZIswZ6Tmhjfu8Gqnz7v0bQLztCFsGMZ+nCzyBwdJtgibK8ST+X"
        cryptrApi = CryptrAPI(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret)
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
                .withHost(equalTo("dev.cryptreu"))
                .willReturn(ok("{}"))
        )
        val org = Organization(name = "Thibaud Paco")
        val createdOrga = cryptrApi?.createOrganization(org)
        //assertEquals(Organization(id = "", name = ""), createdOrga)
    }
}