package cryptr.kotlin

import cryptr.kotlin.enums.Environment
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.User
import cryptr.kotlin.objects.Constants
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

    private fun buildApiPath(resourceName: String, resourceId: String? = null): String {
        val baseApiPath = Constants.API_BASE_BATH + "/" + Constants.API_VERSION + "/" + resourceName
        return if (resourceId != null && resourceId.length > 0) "$baseApiPath/$resourceId" else baseApiPath
    }

    private fun buildOrganizationPath(resourceId: String? = null): String {
        return buildApiPath(Organization.apiResourceName, resourceId)
    }

    private fun buildOrganizationResourcePath(
        organizationDomain: String,
        resourceName: String,
        resourceId: String?
    ): String {
        val baseApiOrgResourcePath =
            Constants.API_BASE_BATH + "/" + Constants.API_VERSION + "/org/" + organizationDomain + "/" + resourceName
        return if (resourceId !== null && resourceId.isNotEmpty()) "$baseApiOrgResourcePath/$resourceId" else baseApiOrgResourcePath
    }

    private fun buildUserPath(organizationDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(organizationDomain, User.apiResourceName, resourceId)
    }


    /**
     * List all [Organization] records according toused API Key
     */

    fun listOrganizations(): ArrayList<Organization> {
        val resp = makeRequest(buildOrganizationPath(), apiKeyToken = retrieveApiKeyToken())
        val organizations: ArrayList<Organization> = ArrayList()
        if (resp !== null) {
            for (i in resp.getJSONArray("data")) {
                organizations.add(Organization(i as JSONObject))
            }
        }
        return organizations
    }

    /**
     * Get Organization from it's id
     *
     * @param domain The id reference of requested Organization
     *
     * @return the requested [Organization]
     */
    fun getOrganization(domain: String): Organization? {
        val resp = makeRequest(buildOrganizationPath(domain), apiKeyToken = retrieveApiKeyToken())
        return resp?.let { Organization(it) }
    }

    /**
     * Creates an [Organization] based on given parameters
     *
     * @param organization The desired [Organization] to create
     *
     * @return the created [Organization]
     */
    fun createOrganization(organization: Organization): Organization? {
        var params = organization.creationMap()
        val resp = makeRequest(buildOrganizationPath(), params, retrieveApiKeyToken())
        return resp?.let { Organization(it) }
    }

    /**
     * List all [User] according to consumed API Key
     *
     * @re
     */
    fun listUsers(organizationDomain: String): ArrayList<User> {
        val resp = makeRequest(buildUserPath(organizationDomain), apiKeyToken = retrieveApiKeyToken())
        val users: ArrayList<User> = ArrayList()
        if (resp !== null) {
            println("resp list Users")
            for (i in resp.getJSONArray("data")) {
                try {
                    val user = User(i as JSONObject)
                    users.add(user)
                } catch (e: Exception) {
                    println("error")
                    println(e.message)
                }
            }
        }
        println(users.size)
        return users
    }

    /**
     * Return the requested [User]
     *
     * @param userId The User resource ID
     *
     * @return The requested [User]
     */
    fun getUser(organizationDomain: String, userId: String): User? {
        val resp = makeRequest(buildUserPath(organizationDomain, userId), apiKeyToken = retrieveApiKeyToken())
        return resp?.let { User(it) }
    }

    // Need createUser fun
    fun createUser(organizationDomain: String, userEmail: String): User? {
        val params = mapOf("profile[email]" to userEmail)
        println(params)
        val resp = makeRequest(buildUserPath(organizationDomain), params, apiKeyToken = retrieveApiKeyToken())
        return resp?.let { User(it) }
    }
}