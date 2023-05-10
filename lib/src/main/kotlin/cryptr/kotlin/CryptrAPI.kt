package cryptr.kotlin

import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.models.*
import cryptr.kotlin.objects.Constants
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.json.JSONObject


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

    private fun handleApiResponse(response: JSONObject): APIResult<CryptrResource, ErrorMessage> {
        return try {
            APISuccess(format.decodeFromString(response.toString()))
        } catch (e: Exception) {
            println("handle APiResponse error")
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * List all [Organization] records according toused API Key
     */

    fun listOrganizations(): APIResult<CryptrResource, ErrorMessage> {
        val resp = makeRequest(buildOrganizationPath(), apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp)
    }

    /**
     * Get Organization from its id
     *
     * @param domain The id reference of requested Organization
     *
     * @return the requested [Organization]
     */
    fun getOrganization(domain: String): APIResult<Organization, ErrorMessage> {
        val resp = makeRequest(buildOrganizationPath(domain), apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Organization, ErrorMessage>
    }

    fun toJSONString(result: APIResult<*, ErrorMessage>): String {
        try {
            return when (result) {
                is APISuccess ->
                    format.encodeToString(CryptrSerializer, result.value as CryptrResource)

                is APIError ->
//                    logInfo { "toto" }.toString()
                    format.encodeToString(ErrorMessage.serializer(), result.error)
            }
        } catch (e: Exception) {
            println("toJSONString error ${e.message}")
            return e.message.toString()
        }
    }

    /**
     * Creates an [Organization] based on given parameters
     *
     * @param organization The desired [Organization] to create
     *
     * @return the created [Organization]
     */
    fun createOrganization(organization: Organization): APIResult<Organization, ErrorMessage> {
        val params = JSONObject(format.encodeToString(organization)).toMap()
        val resp = makeRequest(buildOrganizationPath(), params, retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Organization, ErrorMessage>
    }

    /**
     * Delete a given [Organization]
     *
     * @param organization The [Organization] to delete
     *
     * @return the deleted [Organization]
     */
    fun deleteOrganization(organization: Organization): APIResult<Organization, ErrorMessage> {
        val resp = makeDeleteRequest(buildOrganizationPath(organization.domain), retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Organization, ErrorMessage>
    }

    /**
     * List all [User] according to consumed API Key
     *
     * @param organizationDomain The organization domain where to look for users
     * @return [Listing] with [User]
     */
    fun listUsers(organizationDomain: String): APIResult<Listing<User>, ErrorMessage> {
        val resp = makeRequest(buildUserPath(organizationDomain), apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Listing<User>, ErrorMessage>
    }


    /**
     * Return the requested [User]
     *
     * @param userId The User resource ID
     *
     * @return The requested [User]
     */
    fun getUser(organizationDomain: String, userId: String): APIResult<User, ErrorMessage> {
        val resp = makeRequest(buildUserPath(organizationDomain, userId), apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<User, ErrorMessage>
    }

    /**
     * Creates a [User] based on given email and domain
     *
     * @param organizationDomain The domain of user's organization
     * @param userEmail The mail of the user
     *
     * @return the created [User]
     */
    fun createUser(organizationDomain: String, userEmail: String): APIResult<User, ErrorMessage> {
        return createUser(organizationDomain, user = User(email = userEmail))
    }

    fun createUser(organizationDomain: String, user: User): APIResult<User, ErrorMessage> {
        val params = JSONObject(format.encodeToString(user)).toMap()
        val resp = makeRequest(buildUserPath(organizationDomain), params, apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<User, ErrorMessage>
    }


    /**
     * Delete a given [User]
     *
     * @param user The [User] to delete
     *
     * @return the deleted [User]
     */
    fun deleteUser(user: User): APIResult<User, ErrorMessage> {
        val resp =
            user.resourceDomain?.let { buildUserPath(it, user.id) }
                ?.let { makeDeleteRequest(it, retrieveApiKeyToken()) }
        return resp?.let { handleApiResponse(it) } as APIResult<User, ErrorMessage>
    }

    /**
     * Applications
     */
    fun listApplications(organizationDomain: String): APIResult<Listing<Application>, ErrorMessage> {
        val path = buildApplicationPath(organizationDomain)
        val resp = makeRequest(path, apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Listing<Application>, ErrorMessage>
    }

    fun getApplication(organizationDomain: String, applicationId: String): APIResult<Application, ErrorMessage> {
        val resp =
            makeRequest(buildApplicationPath(organizationDomain, applicationId), apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Application, ErrorMessage>
    }

    fun createApplication(
        organizationDomain: String,
        application: Application
    ): APIResult<Application, ErrorMessage> {
        val params = JSONObject(format.encodeToString(application)).toMap()
        val resp = makeRequest(buildApplicationPath(organizationDomain), params, retrieveApiKeyToken())
        val appResponse = handleApiResponse(resp)
        return appResponse as APIResult<Application, ErrorMessage>
    }


    /**
     * Delete a given [Application]
     *
     * @param application The [Application] to delete
     *
     * @return the deleted [Application]
     */
    fun deleteApplication(application: Application): APIResult<Application, ErrorMessage> {
        val resp =
            application.resourceDomain?.let { buildApplicationPath(it, application.id) }
                ?.let { makeDeleteRequest(it, retrieveApiKeyToken()) }
        return resp?.let { handleApiResponse(it) } as APIResult<Application, ErrorMessage>
    }

}