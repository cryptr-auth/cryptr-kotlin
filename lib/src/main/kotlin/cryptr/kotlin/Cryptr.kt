package cryptr.kotlin

import ch.qos.logback.classic.Level
import cryptr.kotlin.enums.ChallengeType
import cryptr.kotlin.enums.CryptrApiPath
import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.interfaces.Requestable
import cryptr.kotlin.models.*
import cryptr.kotlin.models.connections.SsoConnection
import cryptr.kotlin.models.deleted.DeletedApplication
import cryptr.kotlin.models.deleted.DeletedOrganization
import cryptr.kotlin.models.deleted.DeletedUser
import cryptr.kotlin.objects.Constants
import cryptr.kotlin.objects.Constants.DEFAULT_BASE_URL
import cryptr.kotlin.objects.Constants.DEFAULT_REDIRECT_URL
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * Instantiate Cryptr
 *
 * @param tenantDomain Your account value `domain`
 * @param baseUrl The URL of your Cryptr Service
 * @param defaultRedirectUrl Where you want to redirect te end-user after dev.cryptr.eu process
 * @param apiKeyClientId The ID of your API KEY
 * @param apiKeyClientSecret The secret of your API KEY
 */
open class Cryptr(
    protected val tenantDomain: String = System.getProperty(CryptrEnvironment.CRYPTR_TENANT_DOMAIN.toString()),
    protected val baseUrl: String = System.getProperty(CryptrEnvironment.CRYPTR_BASE_URL.toString(), DEFAULT_BASE_URL),
    protected val defaultRedirectUrl: String = System.getProperty(
        CryptrEnvironment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    protected val apiKeyClientId: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    protected val apiKeyClientSecret: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
) : Requestable {
    @OptIn(ExperimentalSerializationApi::class)
    val format = Json { ignoreUnknownKeys = true; explicitNulls = true; encodeDefaults = true }

    init {
        if (!isJUnitTest()) {
            setLogLevel(Level.INFO.toString())
        }
        logInfo({
            """Cryptr intialized with:
                |- tenantDomain: $tenantDomain
                |- baseUrl: $baseUrl
                |- defaultRedirection: $defaultRedirectUrl
                |- apiKeyClientId: $apiKeyClientId
                |- apiKeyClientSecret: $apiKeyClientSecret
            """.trimMargin()
        })
    }


    fun retrieveApiKeyToken(): String? {
        val tokensFromProperties = setOf(
            System.getProperty("CRYPTR_API_KEY_TOKEN", "null"),
            System.getProperty("CRYPTR_CURRENT_API_TOKEN", "null")
        )
        val tokenFromProperties = tokensFromProperties.firstOrNull { it !== "null" && it.length > 2 }

        if (tokenFromProperties !== null) {
            return tokenFromProperties
        } else {
            val params = mapOf(
                "client_id" to apiKeyClientId,
                "client_secret" to apiKeyClientSecret,
                "tenant_domain" to tenantDomain,
                "grant_type" to "client_credentials"
            )
            Constants.API_BASE_BATH
            val apiKeyTokenResponse =
                makeRequest(
                    "${Constants.API_BASE_BATH}/${Constants.API_VERSION}${CryptrApiPath.API_KEY_TOKEN.pathValue}",
                    baseUrl = baseUrl,
                    params = params
                )
            val apiKeyToken = apiKeyTokenResponse.getString("access_token")
            if (apiKeyToken !== null) {
                System.setProperty("CRYPTR_CURRENT_API_KEY_TOKEN", apiKeyToken)
            }
            return apiKeyToken
        }
    }


    /**
     * HEADLESS
     */

    /**
     * Generate a SSO SAMl Challenge according to dev.cryptr.eu and given parameters
     * orgDomain or userEmail value is required
     *
     * @param redirectUri The endpoint where you will consume after successfull authnetication
     * @param orgDomain Organization domain linked to the targeted SSO Connection
     * @param userEmail End-User email linked to the SSO Connection
     * @return a JSONObject with `authorization_url`that end-user has to open to do his authententication process
     */
    fun createSSOSamlChallenge(
        redirectUri: String = defaultRedirectUrl,
        orgDomain: String? = null,
        userEmail: String? = null
    ): SSOChallenge {
        return createSSOChallenge(redirectUri, orgDomain, userEmail)
    }

    /**
     * Generate a SSO Oauth Challenge according to dev.cryptr.eu and given parameters
     * orgDomain or userEmail value is required
     *
     * @param redirectUri The endpoint where you will consume after successfull authnetication
     * @param orgDomain Organization domain linked to the targeted SSO Connection
     * @param userEmail End-User email linked to the SSO Connection
     * @return a JSONObject with `authorization_url`that end-user has to open to do his authententication process
     */
    fun createSSOOauthChallenge(
        redirectUri: String = defaultRedirectUrl,
        orgDomain: String? = null,
        userEmail: String? = null
    ): SSOChallenge {
        return createSSOChallenge(redirectUri, orgDomain, userEmail, ChallengeType.OAUTH)
    }

    /**
     * Generate a SSO Challenge according to dev.cryptr.eu and given parameters
     * orgDomain or userEmail value is required
     *
     * @param redirectUri The endpoint where you will consume after successfull authnetication
     * @param orgDomain Organization domain linked to the targeted SSO Connection
     * @param userEmail End-User email linked to the SSO Connection
     * @param authType (Optional, Default: SAML)
     * @return a [SSOChallenge] with `authorization_url`that end-user has to open to do his authententication process
     */
    fun createSSOChallenge(
        redirectUri: String = defaultRedirectUrl,
        orgDomain: String? = null,
        userEmail: String? = null,
        authType: ChallengeType? = ChallengeType.SAML
    ): SSOChallenge {
        if (orgDomain != null || userEmail != null) {
            val path = "api/v2/sso-${authType?.value}-challenges"
            val params = if (orgDomain !== null) mapOf(
                "redirect_uri" to redirectUri,
                "org_domain" to orgDomain
            ) else mapOf("redirect_uri" to redirectUri, "user_email" to userEmail)
            val resp = makeRequest(path, baseUrl = baseUrl, params = params, apiKeyToken = retrieveApiKeyToken())
            return format.decodeFromString<SSOChallenge>(resp.toString())
        } else {
            throw Exception("requires either orgDomain or endUser value")
        }
    }

    /**
     * Consumes the code value to retrieve authnetication payload containing end-user JWTs
     *
     * @param code the query param received on your callbakc endpoint(redirectUri from create challentge fun)
     * @return JSONObject containing end-user session JWTs
     */
    fun consumeSSOSamlChallengeCallback(code: String? = ""): JSONObject? {
        if (code !== "" && code !== null) {
            val params = mapOf("code" to code)
            return makeRequest("oauth/token", baseUrl, params = params, apiKeyToken = retrieveApiKeyToken())
        } else {
            throw Exception("code is required")
        }
    }

    /**
     * RESOURCE MANAGEMENT
     */


    private fun handleApiResponse(response: JSONObject): APIResult<CryptrResource, ErrorMessage> {
        return try {
            APISuccess(format.decodeFromString(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }


    /**
     * List all [Organization] records according toused API Key
     */

    fun listOrganizations(): APIResult<Listing<Organization>, ErrorMessage> {
        val resp = makeRequest(buildOrganizationPath(), baseUrl, apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Listing<Organization>, ErrorMessage>
    }

    /**
     * Get Organization from its id
     *
     * @param domain The id reference of requested Organization
     *
     * @return the requested [Organization]
     */
    fun getOrganization(domain: String): APIResult<Organization, ErrorMessage> {
        val resp = makeRequest(buildOrganizationPath(domain), baseUrl = baseUrl, apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Organization, ErrorMessage>
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
        val resp = makeRequest(buildOrganizationPath(), baseUrl, params = params, apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Organization, ErrorMessage>
    }

    /**
     * Delete a given [Organization]
     *
     * @param organization The [Organization] to delete
     *
     * @return the deleted [Organization]
     */
    fun deleteOrganization(organization: Organization): DeletedOrganization? {
        val response = makeDeleteRequest(
            buildOrganizationPath(organization.domain),
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            format.decodeFromString<DeletedOrganization>(response.toString())
        } catch (e: Exception) {
            println("handle APiResponse error")
            logException(e)
            return null
        }
    }

    /**
     * List all [User] according to consumed API Key
     *
     * @param organizationDomain The organization domain where to look for users
     * @return [Listing] with [User]
     */
    fun listUsers(organizationDomain: String): APIResult<Listing<User>, ErrorMessage> {
        val resp =
            makeRequest(buildUserPath(organizationDomain), baseUrl = baseUrl, apiKeyToken = retrieveApiKeyToken())
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
        val resp = makeRequest(
            buildUserPath(organizationDomain, userId),
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken()
        )
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
        val resp = makeRequest(
            buildUserPath(organizationDomain),
            baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return handleApiResponse(resp) as APIResult<User, ErrorMessage>
    }

    /**
     * Updates the given [User]
     *
     * @param userToUpdate The [User] to update
     *
     * @return updated [User]
     */

    fun updateUser(userToUpdate: User): APIResult<User, ErrorMessage> {
        val params = JSONObject(format.encodeToString(userToUpdate)).toMap()
        val response = makeUpdateRequest(
            buildUserPath(userToUpdate.resourceDomain.toString(), userToUpdate.id),
            baseUrl = baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return handleApiResponse(response) as APIResult<User, ErrorMessage>
    }


    /**
     * Delete a given [User]
     *
     * @param user The [User] to delete
     *
     * @return the deleted [User]
     */
    fun deleteUser(user: User): DeletedUser? {
        val response = makeDeleteRequest(
            buildUserPath(user.resourceDomain.toString(), user.id),
            baseUrl = baseUrl,
            retrieveApiKeyToken()
        )
        return try {
            format.decodeFromString<DeletedUser>(response.toString())
        } catch (e: Exception) {
            println("handle APiResponse error")
            logException(e)
            return null
        }
    }

    /**
     * List Organization [Application]s
     *
     * @param organizationDomain Organization's domain
     *
     * @return [APIResult] the response
     */
    fun listApplications(organizationDomain: String): APIResult<Listing<Application>, ErrorMessage> {
        val path = buildApplicationPath(organizationDomain)
        val resp = makeRequest(path, baseUrl = baseUrl, apiKeyToken = retrieveApiKeyToken())
        return handleApiResponse(resp) as APIResult<Listing<Application>, ErrorMessage>
    }

    fun getApplication(organizationDomain: String, applicationId: String): APIResult<Application, ErrorMessage> {
        val resp =
            makeRequest(
                buildApplicationPath(organizationDomain, applicationId),
                baseUrl = baseUrl,
                apiKeyToken = retrieveApiKeyToken()
            )
        return handleApiResponse(resp) as APIResult<Application, ErrorMessage>
    }

    /**
     * Creates an [Application] on your Cryptr service for [Organization provided]
     *
     * @param
     */
    fun createApplication(
        organizationDomain: String,
        application: Application
    ): APIResult<Application, ErrorMessage> {
        val params = JSONObject(format.encodeToString(application)).toMap()
        val resp =
            makeRequest(
                buildApplicationPath(organizationDomain),
                baseUrl = baseUrl,
                params = params,
                apiKeyToken = retrieveApiKeyToken()
            )
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
    fun deleteApplication(application: Application): DeletedApplication? {
        val response = makeDeleteRequest(
            buildApplicationPath(application.resourceDomain.toString(), application.id),
            baseUrl = baseUrl,
            retrieveApiKeyToken()
        )
        return try {
            format.decodeFromString<DeletedApplication>(response.toString())
        } catch (e: Exception) {
            println("handle APiResponse error")
            logException(e)
            return null
//            APIError(ErrorMessage(response.toString()))
        }
    }

    /** Creates a [SsoConnection]
     *
     */
    fun createSSOConnection(
        organizationDomain: String,
        providerType: String? = null,
        applicationId: String? = null,
        ssoAdminEmail: String? = null,
        sendEmail: Boolean? = true
    ): APIResult<SsoConnection, ErrorMessage> {
        val params = mapOf(
            "provider_type" to providerType,
            "application_id" to applicationId,
            "sso_admin_email" to ssoAdminEmail,
            "send_email" to sendEmail
        )
        val resp = makeRequest(
            buildSSOConnectionPath(organizationDomain),
            baseUrl = baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return handleApiResponse(resp) as APIResult<SsoConnection, ErrorMessage>
    }

    /**
     * Creates AdminOnbording
     */
    fun createSSOAdminOnboarding(
        organizationDomain: String,
        ssoAdminEmail: String? = null,
        providerType: String? = null,
        emailTemplateId: String? = null,
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val path = buildAdminOnboardingUrl(organizationDomain, "sso-connections")
        val params = mapOf(
            "it_admin_email" to ssoAdminEmail,
            "provider_type" to providerType,
            "email_template_id" to emailTemplateId
        )
        val resp = makeRequest(
            path,
            baseUrl = baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "POST"
        )
        return handleApiResponse(resp) as APIResult<AdminOnboarding, ErrorMessage>
    }


    /** Invites Admin
     *
     */
    fun inviteAdmin(ssoConnection: SsoConnection): APIResult<CryptrResource, ErrorMessage> {
        val resp = makeRequest(
            buildAdminOnboardingUrl(ssoConnection.resourceDomain.toString(), "sso-connections") + "/invite",
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "POST"
        )

        return handleApiResponse(resp)
    }

    /** Reset SSO Connection Admin onboarding
     *
     */
    fun resetSSOAdminOnboarding(ssoConnection: SsoConnection): APIResult<CryptrResource, ErrorMessage> {
        val resp = makeRequest(
            buildAdminOnboardingUrl(ssoConnection.resourceDomain.toString(), "sso-connections") + "/reset",
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "PATCH"
        )
        return handleApiResponse(resp)

    }


    /**
     * OTHER
     */
    fun toJSONString(result: APIResult<*, ErrorMessage>): String {
        try {
            return when (result) {
                is APISuccess ->
                    format.encodeToString(CryptrSerializer, result.value as CryptrResource)

                is APIError ->
                    format.encodeToString(ErrorMessage.serializer(), result.error)
            }
        } catch (e: Exception) {
            logException(e)
            return e.message.toString()
        }
    }
}