package cryptr.kotlin

import cryptr.kotlin.enums.Environment
import cryptr.kotlin.models.Application
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.Profile
import cryptr.kotlin.models.User
import cryptr.kotlin.objects.Constants
import kotlinx.serialization.decodeFromString


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

    private fun buildApplicationPath(organizationDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(organizationDomain, Application.apiResourceName, resourceId)
    }


    /**
     * List all [Organization] records according toused API Key
     */

    fun listOrganizations(): ArrayList<Organization> {
        val resp = makeRequest(buildOrganizationPath(), apiKeyToken = retrieveApiKeyToken())
        val organizations: ArrayList<Organization> = ArrayList()
        for (i in resp.getJSONArray("data")) {
            organizations.add(format.decodeFromString<Organization>(i.toString()))
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
        return format.decodeFromString<Organization>(resp.toString())
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
        return format.decodeFromString<Organization>(resp.toString())
    }

    /**
     * List all [User] according to consumed API Key
     *
     * @re
     */
    fun listUsers(organizationDomain: String): ArrayList<User> {
        val resp = makeRequest(buildUserPath(organizationDomain), apiKeyToken = retrieveApiKeyToken())
        val users: ArrayList<User> = ArrayList()
        for (i in resp.getJSONArray("data")) {
            try {
                users.add(format.decodeFromString<User>(i.toString()))
            } catch (e: Exception) {
                println("error")
                println(e.message)
            }
        }
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
        return format.decodeFromString<User>(resp.toString())
    }

    /**
     * Creates a [User] based on given email and domain
     *
     * @param organizationDomain The domain of user's organization
     * @param userEmail The mail of the user
     *
     * @return the created [User]
     */
    fun createUser(organizationDomain: String, userEmail: String): User? {
        return createUser(organizationDomain, user = User(Profile(email = userEmail)))
    }

    fun createUser(organizationDomain: String, user: User): User? {
        val params = user.creationMap()
        val resp = makeRequest(buildUserPath(organizationDomain), params, apiKeyToken = retrieveApiKeyToken())
        return format.decodeFromString<User>(resp.toString())
    }

    /**
     * Applications
     */
    fun listApplications(organizationDomain: String): ArrayList<Application> {
        val path = buildApplicationPath(organizationDomain)
//        println(path)
        val resp = makeRequest(path, apiKeyToken = retrieveApiKeyToken())
        val applications: ArrayList<Application> = ArrayList()
//            println(resp)
        for (i in resp.getJSONArray("data")) {
            try {
                applications.add(format.decodeFromString<Application>(i.toString()))
            } catch (e: Exception) {
                println(e.message)
            }
        }
//        println(applications.size)
        return applications
    }

    fun getApplication(organizationDomain: String, applicationId: String): Application? {
        val resp =
            makeRequest(buildApplicationPath(organizationDomain, applicationId), apiKeyToken = retrieveApiKeyToken())
        return format.decodeFromString<Application>(resp.toString())
    }

    fun createApplication(organizationDomain: String, application: Application): Application? {
        var params = application.toJSONObject().toMap()
        val resp = makeRequest(buildApplicationPath(organizationDomain), params, retrieveApiKeyToken())
        return format.decodeFromString(resp.toString())
    }

}