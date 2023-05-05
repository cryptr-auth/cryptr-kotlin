package cryptr.kotlin

import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.models.Application
import cryptr.kotlin.models.Listing
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.User
import cryptr.kotlin.objects.Constants
import kotlinx.serialization.decodeFromString


/**
 * Cryptr to handle resources such as Organization, User, Application...
 */
class CryptrAPI(
    tenantDomain: String = System.getProperty(CryptrEnvironment.CRYPTR_TENANT_DOMAIN.toString()),
    baseUrl: String = System.getProperty(CryptrEnvironment.CRYPTR_BASE_URL.toString(), DEFAULT_BASE_URL),
    defaultRedirectUrl: String = System.getProperty(
        CryptrEnvironment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    apiKeyClientId: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    apiKeyClientSecret: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
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

    fun listOrganizations(): Listing<Organization>? {
        val resp = makeRequest(buildOrganizationPath(), apiKeyToken = retrieveApiKeyToken())
        return try {
            format.decodeFromString<Listing<Organization>>(resp.toString())
        } catch (e: Exception) {
            logException(e)
            null
        }
    }

    /**
     * Get Organization from its id
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
     * @param organizationDomain The organization domain where to look for users
     * @return [Listing] with [User]
     */
    fun listUsers(organizationDomain: String): Listing<User>? {
        val resp = makeRequest(buildUserPath(organizationDomain), apiKeyToken = retrieveApiKeyToken())
        return try {
            format.decodeFromString<Listing<User>>(resp.toString())
        } catch (e: Exception) {
            logException(e)
            null
        }
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
        return createUser(organizationDomain, user = User(email = userEmail))
    }

    fun createUser(organizationDomain: String, user: User): User? {
        val params = user.creationMap()
        val resp = makeRequest(buildUserPath(organizationDomain), params, apiKeyToken = retrieveApiKeyToken())
        return format.decodeFromString<User>(resp.toString())
    }

    /**
     * Applications
     */
    fun listApplications(organizationDomain: String): Listing<Application>? {
        val path = buildApplicationPath(organizationDomain)
        val resp = makeRequest(path, apiKeyToken = retrieveApiKeyToken())
        return try {
            format.decodeFromString<Listing<Application>>(resp.toString())
        } catch (e: Exception) {
            logException(e)
            null
        }
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