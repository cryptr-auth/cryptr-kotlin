package cryptr.kotlin

import cryptr.kotlin.models.Organization
import org.json.JSONObject

/**
 * Cryptr to handle resources such as Organization, User, Application...
 */
class CryptrAPI(
    tenantDomain: String = System.getProperty(Environment.CRYPTR_TENANT_DOMAIN.toString()),
    baseUrl: String = System.getProperty(Environment.CRYPTR_BASE_URL.toString(), DEFAULT_BASE_URL),
    defaultRedirectUrl: String = System.getProperty(
        Environment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    apiKeyClientId: String = System.getProperty(Environment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    apiKeyClientSecret: String = System.getProperty(Environment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
) : Cryptr(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret) {

    fun listOrganizations(): ArrayList<Organization> {
        val resp = makeRequest("api/v2/organizations", apiKeyToken = retrieveApiKeyToken())
        val organizations: ArrayList<Organization> = ArrayList()
        if (resp !== null) {
            for (i in resp.getJSONArray("data")) {
                organizations.add(Organization(i as JSONObject))
            }
        }
        return organizations
    }

    fun getOrganization(id: String): Organization? {
        val resp = makeRequest("api/v2/organizations/$id", apiKeyToken = retrieveApiKeyToken())
        return resp?.let { Organization(it) }
    }

    fun createOrganization(organization: Organization): Organization? {
        var params = organization.creationMap()
        val resp = makeRequest("/api/v2/organizations", params, retrieveApiKeyToken())
        return resp?.let { Organization(it) }
    }
}