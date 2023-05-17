package cryptr.kotlin

import ch.qos.logback.classic.Level
import cryptr.kotlin.enums.ChallengeType
import cryptr.kotlin.enums.CryptrApiPath
import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.interfaces.Requestable
import cryptr.kotlin.interfaces.Tokenable
import cryptr.kotlin.models.*
import cryptr.kotlin.models.List
import cryptr.kotlin.models.connections.SsoConnection
import cryptr.kotlin.models.deleted.DeletedApplication
import cryptr.kotlin.models.deleted.DeletedOrganization
import cryptr.kotlin.models.deleted.DeletedUser
import cryptr.kotlin.models.jwt.JWTToken
import cryptr.kotlin.objects.Constants
import cryptr.kotlin.objects.Constants.DEFAULT_BASE_URL
import cryptr.kotlin.objects.Constants.DEFAULT_REDIRECT_URL
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
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
class Cryptr(
    protected val tenantDomain: String = System.getProperty(CryptrEnvironment.CRYPTR_TENANT_DOMAIN.toString()),
    protected val baseUrl: String = System.getProperty(CryptrEnvironment.CRYPTR_BASE_URL.toString(), DEFAULT_BASE_URL),
    protected val defaultRedirectUrl: String = System.getProperty(
        CryptrEnvironment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    protected val apiKeyClientId: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    protected val apiKeyClientSecret: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
) : Requestable, Tokenable {
    @OptIn(ExperimentalSerializationApi::class)
    val format = Json { ignoreUnknownKeys = true; explicitNulls = true; encodeDefaults = true }
    private val ignoreIssChecking = System.getProperty("CRYPTR_IGNORE_ISS_CHECKING", "true") == "true"

    val cryptrBaseUrl: String
        get() = baseUrl

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
            System.getProperty("CRYPTR_CURRENT_API_KEY_TOKEN", "null")
        )
        val tokenFromProperties = tokensFromProperties.firstOrNull { it !== "null" && it.length > 2 }
        if (tokenFromProperties !== null) {
            val verification = verifyApiKeyToken(tokenFromProperties)
            if (verification is JWTToken && verification.validIss) return tokenFromProperties
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
            val verification = verifyApiKeyToken(apiKeyToken, true)
            logDebug({ "verification request $verification" })
            if (verification is JWTToken && verification.validIss) return apiKeyToken
        }
        logError({ "Error while retrieving api key token" })
        return null
    }

    private fun verifyApiKeyToken(apiKeyToken: String, storeInProperties: Boolean? = false): JWTToken? {
        val forceIss = ignoreIssChecking || isJUnitTest()
        val jwtToken = verify(baseUrl, apiKeyToken, forceIss)

        if (storeInProperties == true) {
            System.setProperty("CRYPTR_CURRENT_API_KEY_TOKEN", apiKeyToken)
        }

        return jwtToken
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
    ): APIResult<SSOChallenge, ErrorMessage> {
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
    ): APIResult<SSOChallenge, ErrorMessage> {
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
    ): APIResult<SSOChallenge, ErrorMessage> {
        if (orgDomain != null || userEmail != null) {
            val path = "api/v2/sso-${authType?.value}-challenges"
            val params = if (orgDomain !== null) mapOf(
                "redirect_uri" to redirectUri,
                "org_domain" to orgDomain
            ) else mapOf("redirect_uri" to redirectUri, "user_email" to userEmail)
            val response = makeRequest(path, baseUrl = baseUrl, params = params, apiKeyToken = retrieveApiKeyToken())
            return try {
                APISuccess(format.decodeFromString<SSOChallenge>(response.toString()))
            } catch (e: Exception) {
                logException(e)
                APIError(ErrorMessage(response.toString()))
            }
        } else {
            throw Exception("requires either orgDomain or endUser value")
        }
    }

    /**
     * Consumes the code value to retrieve authnetication payload containing end-user JWTs
     *
     * @param code the query param received on your callback endpoint(redirectUri from create challenge fun)
     * @return JSONObject containing end-user session JWTs
     */
    fun consumeSSOSamlChallengeCallback(code: String? = ""): APIResult<ChallengeResponse, ErrorMessage> {
        if (code !== "" && code !== null) {
            val params = mapOf("code" to code)
            val response = makeRequest("oauth/token", baseUrl, params = params, apiKeyToken = retrieveApiKeyToken())
            return try {
                APISuccess(format.decodeFromString<ChallengeResponse>(response.toString()))
            } catch (e: Exception) {
                logException(e)
                APIError(ErrorMessage(response.toString()))
            }
        } else {
            return APIError(ErrorMessage("code is required"))
        }
    }


    /**
     * List all [Organization] records according toused API Key
     */

    fun listOrganizations(perPage: Int? = 10, currentPage: Int? = 1): APIResult<List<Organization>, ErrorMessage> {
        val response =
            makeListRequest(buildOrganizationPath(), baseUrl, apiKeyToken = retrieveApiKeyToken(), perPage, currentPage)
        return try {
            APISuccess(format.decodeFromString<List<Organization>>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Get Organization from its id
     *
     * @param domain The id reference of requested Organization
     *
     * @return the requested [Organization]
     */
    fun getOrganization(domain: String): APIResult<Organization, ErrorMessage> {
        val response =
            makeRequest(buildOrganizationPath(domain), baseUrl = baseUrl, apiKeyToken = retrieveApiKeyToken())
        return try {
            APISuccess(format.decodeFromString<Organization>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
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
        val response =
            makeRequest(buildOrganizationPath(), baseUrl, params = params, apiKeyToken = retrieveApiKeyToken())
        return try {
            APISuccess(format.decodeFromString<Organization>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
            logException(e)
            return null
        }
    }

    /**
     * List all [User] according to consumed API Key
     *
     * @param organizationDomain The organization domain where to look for users
     * @return [List] with [User]
     */
    fun listUsers(
        organizationDomain: String,
        perPage: Int? = 10,
        currentPage: Int? = 1
    ): APIResult<List<User>, ErrorMessage> {
        val response =
            makeListRequest(
                buildUserPath(organizationDomain),
                baseUrl = baseUrl,
                apiKeyToken = retrieveApiKeyToken(),
                perPage,
                currentPage
            )
        return try {
            APISuccess(format.decodeFromString<List<User>>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }


    /**
     * Return the requested [User]
     *
     * @param userId The User resource ID
     *
     * @return The requested [User]
     */
    fun getUser(organizationDomain: String, userId: String): APIResult<User, ErrorMessage> {
        val response = makeRequest(
            buildUserPath(organizationDomain, userId),
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<User>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
        val response = makeRequest(
            buildUserPath(organizationDomain),
            baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<User>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
        return try {
            APISuccess(format.decodeFromString<User>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
    fun listApplications(
        organizationDomain: String,
        perPage: Int? = 10,
        currentPage: Int? = 1
    ): APIResult<List<Application>, ErrorMessage> {
        val path = buildApplicationPath(organizationDomain)
        val response =
            makeListRequest(path, baseUrl = baseUrl, apiKeyToken = retrieveApiKeyToken(), perPage, currentPage)
        return try {
            APISuccess(format.decodeFromString<List<Application>>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    fun getApplication(organizationDomain: String, applicationId: String): APIResult<Application, ErrorMessage> {
        val response =
            makeRequest(
                buildApplicationPath(organizationDomain, applicationId),
                baseUrl = baseUrl,
                apiKeyToken = retrieveApiKeyToken()
            )
        return try {
            APISuccess(format.decodeFromString<Application>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
        val response =
            makeRequest(
                buildApplicationPath(organizationDomain),
                baseUrl = baseUrl,
                params = params,
                apiKeyToken = retrieveApiKeyToken()
            )
        return try {
            APISuccess(format.decodeFromString<Application>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
        val response = makeRequest(
            buildSSOConnectionPath(organizationDomain),
            baseUrl = baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<SsoConnection>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
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
        val response = makeRequest(
            path,
            baseUrl = baseUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "POST"
        )
        return try {
            APISuccess(format.decodeFromString<AdminOnboarding>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }


    /** Invites Admin
     *
     */
    fun inviteAdmin(ssoConnection: SsoConnection): APIResult<CryptrResource, ErrorMessage> {
        val response = makeRequest(
            buildAdminOnboardingUrl(ssoConnection.resourceDomain.toString(), "sso-connections") + "/invite",
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "POST"
        )

        return try {
            APISuccess(format.decodeFromString<CryptrResource>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /** Reset SSO Connection Admin onboarding
     *
     */
    fun resetSSOAdminOnboarding(ssoConnection: SsoConnection): APIResult<CryptrResource, ErrorMessage> {
        val response = makeRequest(
            buildAdminOnboardingUrl(ssoConnection.resourceDomain.toString(), "sso-connections") + "/reset",
            baseUrl = baseUrl,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "PATCH"
        )
        return try {
            APISuccess(format.decodeFromString<CryptrResource>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }

    }


    /**
     * OTHER
     */
    fun toJSONListString(result: APIResult<List<CryptrResource>, ErrorMessage>): String {
        return try {
            when (result) {
                is APISuccess ->
                    JSONObject()
                        .put("total", result.value.total)
                        .put(
                            "data",
                            JSONArray("[" + result.value.data.map { toJSONString(it) }.joinToString(",") + "]")
                        )
                        .put("pagination", JSONObject(format.encodeToString<Pagination>(result.value.pagination)))
                        .toString()

                is APIError ->
                    format.encodeToString(ErrorMessage.serializer(), result.error)
            }
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    fun toJSONString(result: APIResult<*, ErrorMessage>): String {
        return try {
            when (result) {
                is APISuccess ->
                    format.encodeToString(CryptrSerializer, result.value as CryptrResource)

                is APIError ->
                    format.encodeToString(ErrorMessage.serializer(), result.error)
            }
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    fun toJSONString(result: ChallengeResponse): String {
        return try {
            format.encodeToString(ChallengeResponse.serializer(), result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    fun toJSONString(result: ErrorMessage): String {
        return try {
            format.encodeToString(ErrorMessage.serializer(), result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    fun toJSONString(result: CryptrResource): String {
        return try {
            format.encodeToString(CryptrSerializer, result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    fun toJSONString(result: DeletedResource): String {
        return try {
            format.encodeToString(result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }
}