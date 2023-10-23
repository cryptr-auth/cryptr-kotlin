package cryptr.kotlin

import ch.qos.logback.classic.Level
import cryptr.kotlin.enums.ChallengeType
import cryptr.kotlin.enums.CryptrApiPath
import cryptr.kotlin.enums.CryptrEnvironment
import cryptr.kotlin.interfaces.Requestable
import cryptr.kotlin.interfaces.Tokenable
import cryptr.kotlin.models.*
import cryptr.kotlin.models.List
import cryptr.kotlin.models.connections.PasswordConnection
import cryptr.kotlin.models.connections.SSOConnection
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
 * Instantiate Cryptr SDK
 *
 * @param accountDomain Your account value `domain`
 * @param serviceUrl The URL of your Cryptr Service
 * @param defaultRedirectUri Where you want to redirect te end-user after dev.cryptr.eu process
 * @param apiKeyClientId The ID of your API KEY
 * @param apiKeyClientSecret The secret of your API KEY
 */
class Cryptr(
    protected val accountDomain: String = System.getProperty(CryptrEnvironment.CRYPTR_ACCOUNT_DOMAIN.toString()),
    protected val serviceUrl: String = System.getProperty(
        CryptrEnvironment.CRYPTR_BASE_URL.toString(),
        DEFAULT_BASE_URL
    ),
    protected val defaultRedirectUri: String = System.getProperty(
        CryptrEnvironment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    protected val apiKeyClientId: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    protected val apiKeyClientSecret: String = System.getProperty(CryptrEnvironment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
) : Requestable, Tokenable {
    /**
     * @suppress
     */
    @OptIn(ExperimentalSerializationApi::class)
    val format = Json { ignoreUnknownKeys = true; explicitNulls = false; encodeDefaults = false }
    private val ignoreIssChecking = System.getProperty("CRYPTR_IGNORE_ISS_CHECKING", "true") == "true"

    /**
     * Retrieve the current base URL
     */
    val cryptrServiceUrl: String
        get() = serviceUrl

    init {
        if (!isJUnitTest()) {
            setLogLevel(Level.INFO.toString())
        }
        logInfo({
            """Cryptr intialized with:
                |- accountDomain: $accountDomain
                |- serviceUrl: $serviceUrl
                |- defaultRedirectUri: $defaultRedirectUri
                |- apiKeyClientId: $apiKeyClientId
                |- apiKeyClientSecret: $apiKeyClientSecret
            """.trimMargin()
        })
    }


    /**
     * Retrieve the current token using API KEY configuration.
     *
     * You can setup a static using `CRYPTR_API_KEY_TOKEN` property.
     * If not, the SDK will generate one depending on {@link Cryptr#apiKeyClientId} {@link #apiKeyClientId}  and **apiKeyClientSecret**
     * and store it until it's expired
     *
     *
     *
     * @return The api Key token [String] if succeeded
     */
    fun retrieveApiKeyToken(iteration: String? = "1"): String? {
        val tokensFromProperties = setOf(
            System.getProperty("CRYPTR_API_KEY_TOKEN", "null"),
            System.getProperty("CRYPTR_CURRENT_API_KEY_TOKEN", "null")
        )
        val tokenFromProperties = tokensFromProperties.firstOrNull { it !== "null" && it.length > 2 }
        if (tokenFromProperties !== null) {
            val verification = verifyApiKeyToken(tokenFromProperties)
            if (verification is JWTToken && verification.validIss) {
                return tokenFromProperties
            } else {
                System.clearProperty("CRYPTR_CURRENT_API_KEY_TOKEN")
                if (iteration != "2") return retrieveApiKeyToken("2")
            }
        } else {
            val params = mapOf(
                "client_id" to apiKeyClientId,
                "client_secret" to apiKeyClientSecret,
                "tenant_domain" to accountDomain,
                "grant_type" to "client_credentials"
            )
            Constants.API_BASE_BATH
            val apiKeyTokenResponse =
                makeRequest(
                    "${Constants.API_BASE_BATH}/${Constants.API_VERSION}${CryptrApiPath.API_KEY_TOKEN.pathValue}",
                    serviceUrl = serviceUrl,
                    params = params
                )
            val apiKeyToken = apiKeyTokenResponse.getString("access_token")
            val verification = verifyApiKeyToken(apiKeyToken, true)
            logDebug({ "verification request $verification" })
            if (verification is JWTToken && verification.validIss) {
                return apiKeyToken
            } else {
                System.clearProperty("CRYPTR_CURRENT_API_KEY_TOKEN")
            }
        }
        logError({ "Error while retrieving api key token" })
        return null
    }

    private fun verifyApiKeyToken(apiKeyToken: String, storeInProperties: Boolean? = false): JWTToken? {
        val forceIss = ignoreIssChecking || isJUnitTest()
        logDebug({ "apiKeyToken" })
        logDebug({ apiKeyToken })
        val jwtToken = verify(serviceUrl, apiKeyToken, forceIss)

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
    fun createSsoSamlChallenge(
        redirectUri: String = defaultRedirectUri,
        orgDomain: String? = null,
        userEmail: String? = null
    ): APIResult<SSOChallenge, ErrorMessage> {
        return createSsoChallenge(redirectUri, orgDomain, userEmail)
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
    fun createSsoOauthChallenge(
        redirectUri: String = defaultRedirectUri,
        orgDomain: String? = null,
        userEmail: String? = null
    ): APIResult<SSOChallenge, ErrorMessage> {
        return createSsoChallenge(redirectUri, orgDomain, userEmail, ChallengeType.OAUTH)
    }

    /**
     * Generate a SSO Challenge according to dev.cryptr.eu and given parameters
     * orgDomain or userEmail value is required
     *
     * @param redirectUri The endpoint where you will consume after successfull authnetication
     * @param orgDomain Organization domain linked to the targeted SSO Connection
     * @param userEmail End-User email linked to the SSO Connection
     * @param authType (Optional, Default: SAML)
     * @return a [SSOChallenge] with `authorization_url`that end-user has to open to do his authentication process
     */
    fun createSsoChallenge(
        redirectUri: String = defaultRedirectUri,
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
            val response =
                makeRequest(path, serviceUrl = serviceUrl, params = params, apiKeyToken = retrieveApiKeyToken())
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
     * Generate a Password Challenge and given parameters
     * orgDomain and userEmail values are required
     *
     * @param orgDomain Organization's domain linked to the password connection
     * @param userEmail End-User's email
     * @param plaintText the plaint text to authenticate
     *
     * @return [APIResult] with the created [PasswordChallenge]
     */
    fun createPasswordChallenge(
        orgDomain: String,
        userEmail: String,
        plaintText: String? = null,
    ): APIResult<PasswordChallenge, ErrorMessage> {
        val params = mapOf(
            "org_domain" to orgDomain,
            "user_email" to userEmail,
            "plain_text" to plaintText
        )
        val response = makeRequest(
            path = "api/v2/password-challenge",
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<PasswordChallenge>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Generates a Magic Link Challenge from given parameters
     * userEmail and redirectUri are required where orgDomain is optional
     *
     * @param userEmail End-user's email
     * @param redirectUri The endpoint where you will consume after successfull authnetication
     * @param orgDomain Organization's domain linked to the magioc link connection (useful when multiple orgs on same email domain)
     *
     * @return [ApiResult] with the created [MagicLinkChallenge]
     */
    fun createMagicLinkChallenge(
        userEmail: String,
        redirectUri: String,
        orgDomain: String? = null
    ): APIResult<MagicLinkChallenge, ErrorMessage> {
        logDebug({ "createMagicLinkChallenge" })
        val params = mapOf(
            "user_email" to userEmail,
            "redirect_uri" to redirectUri,
            "org_domain" to orgDomain
        )

        val response = makeRequest(
            path = "api/v2/magic-link-challenge",
            serviceUrl, params, apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<MagicLinkChallenge>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Gets tokens from [PasswordChallenge]'s code
     * @param passwordCode PasswordChallenge code after success
     *
     * @return [APIResult] of [PasswordChallengeResponse] containing generated tokens
     */
    fun getPasswordChallengeTokens(passwordCode: String? = null): APIResult<PasswordChallengeResponse, ErrorMessage> {
        if (passwordCode.isNullOrEmpty()) {
            return APIError(ErrorMessage("password challenge code missing"))
        }
        val params = mapOf(
            "grant_type" to "authorization_code",
            "code" to passwordCode
        )
        val response = makeRequest(
            path = "/api/v2/oauth/token",
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<PasswordChallengeResponse>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Creates a password request for the email end-user owner thats redirects to the desired redirect URI
     *
     * @param userEmail The end-user for whom you want to generate the password request
     * @param redirectUri Where you want to redirect the user after his magic link click. Endpoint requires to handle
     * the response
     * @param orgDomain The domain of the [Organization] that owns the user
     *
     * @return an [APIResult] of the successful request
     */
    fun createPasswordRequest(
        userEmail: String,
        redirectUri: String,
        orgDomain: String
    ): APIResult<CryptrResource, ErrorMessage> {
        val params = mapOf(
            "org_domain" to orgDomain,
            "user_email" to userEmail,
            "redirect_uri" to redirectUri
        )
        val response = makeRequest(
            path = buildApiPath("password-request"),
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Creates a password for a given passwordCode and plainText
     * @param passwordCode String given by Cryptr to allow the password update
     * @param plaintText New password value
     *
     * @return an [APIResult] with the created [Password]
     */
    fun createPassword(
        passwordCode: String,
        plaintText: String
    ): APIResult<Password, ErrorMessage> {
        val params = mapOf(
            "password_code" to passwordCode,
            "plain_text" to plaintText
        )
        val response = makeRequest(
            path = buildApiPath("password"),
            serviceUrl,
            params,
            retrieveApiKeyToken()
        )

        return try {
            APISuccess(format.decodeFromString<Password>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Creates a password depending on given parameters
     * @param userEmail End-user's email
     * @param plaintText Password string value
     * @param passwordCode String to allow password creation
     * @param orgDomain Domain of [Organization] that owns the user
     *
     * @return an [APIResult] with the created [Password]
     *
     */
    fun createPassword(
        userEmail: String,
        plaintText: String,
        passwordCode: String,
        orgDomain: String
    ): APIResult<Password, ErrorMessage> {
        val params = mapOf(
            "org_domain" to orgDomain,
            "user_email" to userEmail,
            "plain_text" to plaintText,
            "password_code" to passwordCode
        )

        val response = makeRequest(path = buildApiPath("password"), serviceUrl, params, retrieveApiKeyToken())
        return try {
            APISuccess(format.decodeFromString<Password>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Creates a Password without using the email verification process. CAUTION  this process is not recommended
     *
     * @param userEmail email address of end-user
     * @param plaintText password string value
     * @param orgDomain domain of [Organization] that owns the end-user
     *
     * @return [APIResult] with created [Password]
     *
     */
    fun createPasswordWithoutEmailVerification(
        userEmail: String,
        plaintText: String,
        orgDomain: String,
    ): APIResult<Password, ErrorMessage> {
        val params = mapOf(
            "user_email" to userEmail,
            "plain_text" to plaintText,
            "org_domain" to orgDomain,
        )
        val response = makeRequest(path = buildApiPath("password"), serviceUrl, params, retrieveApiKeyToken())
        return try {
            APISuccess(format.decodeFromString<Password>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Gets tokens from [PasswordChallenge]
     * @param passwordChallenge PasswordChallenge
     */
    fun getPasswordChallengeTokens(passwordChallenge: PasswordChallenge): APIResult<PasswordChallengeResponse, ErrorMessage> {
        return getPasswordChallengeTokens(passwordChallenge.code)
    }


    /**
     * Consumes the code value to retrieve authentication payload containing end-user JWTs
     *
     * @param code the query param received on your callback endpoint(redirectUri from create challenge fun)
     * @return JSONObject containing end-user session JWTs
     */
    fun validateSsoChallenge(code: String? = ""): APIResult<ChallengeResponse, ErrorMessage> {
        if (code !== "" && code !== null) {
            val params = mapOf("code" to code)
            val response = makeRequest("oauth/token", serviceUrl, params = params, apiKeyToken = retrieveApiKeyToken())
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
     * Consumes the code value to retrieve authentication payload containing end-user JWTs
     *
     * @param code the query param received on your callback endpoint(redirectUri from create challenge fun)
     * @return [ChallengeResponse] containing end-user session JWTs
     */
    fun validateChallenge(code: String? = null): APIResult<ChallengeResponse, ErrorMessage> {
        if (code !== null && code.isNotEmpty() && code.isNotBlank()) {
            val params = mapOf("code" to code, "grant_type" to "authorization_code")
            val response =
                makeRequest(path = "api/v2/oauth/token", serviceUrl, params, apiKeyToken = retrieveApiKeyToken())
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
     * Consumes the code value to retrieve authentication payload containing end-user JWTs
     *
     * @param code the query param received on your callback endpoint(redirectUri from create challenge fun)
     * @return [ChallengeResponse] containing end-user session JWTs
     */
    fun validateMagicLinkChallenge(code: String? = null): APIResult<ChallengeResponse, ErrorMessage> {
        if (code !== null && code.isNotEmpty() && code.isNotBlank()) {
            val params = mapOf(
                "code" to code,
                "grant_type" to "authorization_code"
            )
            val response = makeRequest(
                path = "api/v2/oauth/token",
                serviceUrl, params,
                apiKeyToken = retrieveApiKeyToken()
            )
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
            makeListRequest(
                buildOrganizationPath(),
                serviceUrl,
                apiKeyToken = retrieveApiKeyToken(),
                perPage,
                currentPage
            )
        return try {
            APISuccess(format.decodeFromString<List<Organization>>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Get Organization from its domain
     *
     * @param domain The domain reference of requested Organization
     *
     * @return the requested [Organization]
     */
    fun retrieveOrganization(domain: String): APIResult<Organization, ErrorMessage> {
        val response =
            makeRequest(buildOrganizationPath(domain), serviceUrl = serviceUrl, apiKeyToken = retrieveApiKeyToken())
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
     * @param name The desired name for Organization to create
     * @param allowedEmailDomains (Optional) email domains for end-user
     *
     * @return the created [Organization]
     */
    fun createOrganization(
        name: String,
        allowedEmailDomains: Set<String>? = null
    ): APIResult<Organization, ErrorMessage> {
        return if (allowedEmailDomains.isNullOrEmpty()) {
            createOrganization(Organization(name = name))
        } else {
            createOrganization(Organization(name = name, allowedEmailDomains = allowedEmailDomains))
        }
    }


    /**
     * Creates an [Organization] based on given Organization Structure
     *
     * @param organization The desired [Organization] to create
     *
     * @return the created [Organization]
     */
    fun createOrganization(organization: Organization): APIResult<Organization, ErrorMessage> {
        val params = JSONObject(format.encodeToString(organization)).toMap()
        val response =
            makeRequest(buildOrganizationPath(), serviceUrl, params = params, apiKeyToken = retrieveApiKeyToken())
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
            serviceUrl = serviceUrl,
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
     * @param orgDomain The organization domain where to look for users
     * @return [List] with [User]
     */
    fun listUsers(
        orgDomain: String,
        perPage: Int? = 10,
        currentPage: Int? = 1
    ): APIResult<List<User>, ErrorMessage> {
        val response =
            makeListRequest(
                buildUserPath(orgDomain),
                serviceUrl = serviceUrl,
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
    fun retrieveUser(orgDomain: String, userId: String): APIResult<User, ErrorMessage> {
        val response = makeRequest(
            buildUserPath(orgDomain, userId),
            serviceUrl = serviceUrl,
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
     * @param orgDomain The domain of user's organization
     * @param userEmail The mail of the user
     *
     * @return the created [User]
     */
    fun createUser(orgDomain: String, userEmail: String): APIResult<User, ErrorMessage> {
        return createUser(orgDomain, user = User(email = userEmail))
    }

    /**
     * Creates a [User] based on given structure and domain
     *
     * @param orgDomain The domain of user's organization
     * @param user The [User] structure
     *
     * @return the created [User]
     */
    fun createUser(orgDomain: String, user: User): APIResult<User, ErrorMessage> {
        val params = JSONObject(format.encodeToString(user)).toMap()
        val response = makeRequest(
            buildUserPath(orgDomain),
            serviceUrl,
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
            serviceUrl = serviceUrl,
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
            serviceUrl = serviceUrl,
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
     * @param orgDomain Organization's domain
     *
     * @return [APIResult] the response
     */
    fun listApplications(
        orgDomain: String,
        perPage: Int? = 10,
        currentPage: Int? = 1
    ): APIResult<List<Application>, ErrorMessage> {
        val path = buildApplicationPath(orgDomain)
        val response =
            makeListRequest(path, serviceUrl = serviceUrl, apiKeyToken = retrieveApiKeyToken(), perPage, currentPage)
        return try {
            APISuccess(format.decodeFromString<List<Application>>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Retrieves an [Application] based on its Organization's domain and ID
     *
     * @param orgDomain The [Organization] domain
     * @param applicationId The ID of the requested [Application]
     *
     * @return [Application]
     */
    fun retrieveApplication(orgDomain: String, applicationId: String): APIResult<Application, ErrorMessage> {
        val response =
            makeRequest(
                buildApplicationPath(orgDomain, applicationId),
                serviceUrl = serviceUrl,
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
        orgDomain: String,
        application: Application
    ): APIResult<Application, ErrorMessage> {
        val params = JSONObject(format.encodeToString(application)).toMap()
        val response =
            makeRequest(
                buildApplicationPath(orgDomain),
                serviceUrl = serviceUrl,
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
            serviceUrl = serviceUrl,
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

    /** Creates a [SSOConnection]
     *
     */
    fun createSsoConnection(
        orgDomain: String,
        providerType: String? = null,
        applicationId: String? = null,
        itAdminEmail: String? = null,
        sendEmail: Boolean? = true
    ): APIResult<SSOConnection, ErrorMessage> {
        val params = mapOf(
            "provider_type" to providerType,
            "application_id" to applicationId,
            "sso_admin_email" to itAdminEmail,
            "send_email" to sendEmail
        )
        val response = makeRequest(
            buildOrganizationResourcePath(orgDomain, resourceName = "sso-connection"),
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            APISuccess(format.decodeFromString<SSOConnection>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /** Creates a [PasswordConnection]
     * @param orgDomain REQUIRED domain of the organization
     */
    fun createPasswordConnection(
        orgDomain: String,
        plainTextMinLength: Int? = null,
        plainTextMaxLength: Int? = null,
        forgotPasswordTemplateId: String? = null,
        pepperRotationPeriod: Int? = null,
    ): APIResult<PasswordConnection, ErrorMessage> {
        val params = mapOf(
            "plain_text_min_length" to plainTextMinLength,
            "plain_text_max_length" to plainTextMaxLength,
            "forgot_password_template_id" to forgotPasswordTemplateId,
            "pepper_rotation_period" to pepperRotationPeriod,
        ).filterValues { it != null }
        val response = makeRequest(
            buildOrganizationResourcePath(orgDomain, resourceName = "password-connection"),
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken()
        )
        return try {
            val createdPasswordConn = format.decodeFromString<PasswordConnection>(response.toString())
            if (createdPasswordConn.id !== null) {
                APISuccess(createdPasswordConn)
            } else {
                APIError(ErrorMessage(response.toString()))
            }
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * List all your SSOConnections
     *
     * @param perPage How many item desired in le listing
     * @param currentPage Desired current page
     *
     * @since 0.1.3
     */
    fun listSsoConnections(
        perPage: Int? = null,
        currentPage: Int? = null
    ): APIResult<List<SSOConnection>, ErrorMessage> {
        val response = makeListRequest(
            buildApiPath("sso-connections"),
            serviceUrl = serviceUrl,
            apiKeyToken = retrieveApiKeyToken(),
            perPage, currentPage
        )
        return try {
            APISuccess(format.decodeFromString<List<SSOConnection>>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * retrieve the [SSOConnection] of an [Organization]
     *
     * @param orgDomain The domain of the organization
     *
     * @since 0.1.3
     */
    fun retrieveSsoConnection(orgDomain: String): APIResult<SSOConnection, ErrorMessage> {
        val response = makeRequest(
            buildOrganizationResourcePath(orgDomain, "sso-connection"),
            serviceUrl = serviceUrl,
            apiKeyToken = retrieveApiKeyToken()
        )

        return try {
            APISuccess(format.decodeFromString<SSOConnection>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Update an SSOConnection for desired [Orgnaization]
     *
     * @param orgDomain The domain of the Organization
     * @param params Map of desired changes for the Organization's SSOConnection
     *
     * @since 0.1.3
     */
    fun updateSsoConnection(
        orgDomain: String,
        params: Map<String, Any>,
    ): APIResult<SSOConnection, ErrorMessage> {
        val response = makeUpdateRequest(
            buildOrganizationResourcePath(orgDomain, "sso-connection"),
            serviceUrl = serviceUrl,
            apiKeyToken = retrieveApiKeyToken(),
            params = params
        )
        return try {
            APISuccess(format.decodeFromString<SSOConnection>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Creates [APIResult]  of [AdminOnboarding] type `sso-connection`
     *
     * @param orgDomain The domain of the targeted [Organization]
     * @param itAdminEmail (Optional) [String] email for the Organization's IT Admin able to configure SSO
     * @param providerType (Optional) [String] provider type for the SSO to setup (ex : Okta, ADFS...)
     * @param emailTemplateId (Optional) [String] ID of the email template to use for email send to IT Admin
     * @param sendEmail (Optional) [Boolean] to send immediately email to IT Admin or not (default: true)
     * @param applicationId (Optional) [String] ID of the frontend CLient application to redirect end-user to after SSO
     * authentication
     *
     * @return [APIResult] of the creation result
     */
    fun createSsoAdminOnboarding(
        orgDomain: String,
        itAdminEmail: String,
        providerType: String? = null,
        emailTemplateId: String? = null,
        sendEmail: Boolean? = true,
        applicationId: String? = null,
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val customParams = mapOf(
            "provider_type" to providerType,
            "application_id" to applicationId
        )
        return createAdminOnboarding(
            orgDomain, "sso-connection", itAdminEmail, emailTemplateId, sendEmail, customParams
        )
    }

    /**
     * Creates [APIResult]  of [AdminOnboarding] according to provided params
     *
     * @param orgDomain The domain of the targeted [Organization]
     * @param onboardingType [String] representing type of onboarding (ex: `sso-connection`)
     * @param itAdminEmail (Optional) [String] email for the Organization's IT Admin able to configure SSO
     * @param emailTemplateId (Optional) [String] ID of the email template to use for email send to IT Admin
     * @param sendEmail (Optional) [Boolean] to send immediately email to IT Admin or not (default: true)
     * @param customParams (Optional) [Map] of custom attributes related to onboardingType
     *
     * @return [APIResult] of the creation result
     */
    fun createAdminOnboarding(
        orgDomain: String,
        onboardingType: String,
        itAdminEmail: String? = null,
        emailTemplateId: String? = null,
        sendEmail: Boolean? = true,
        customParams: Map<String, Any?>? = mapOf<String, Any?>()
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val path = buildAdminOnboardingUrl(orgDomain, onboardingType)
        var basicParams: Map<String, Any?> = mapOf(
            "it_admin_email" to itAdminEmail,
            "email_template_id" to emailTemplateId,
            "send_email" to sendEmail,
            "onboarding_type" to onboardingType
        )
        val params = basicParams.toMutableMap()
        try {
            customParams?.map { params.put(it.key, it.value) }
        } catch (pe: Exception) {
            logException(pe)
            logError({ pe.message })
        }
        logDebug({ params.toString() })
        val response = makeRequest(
            path,
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "POST"
        )
        return try {
            APISuccess(format.decodeFromString<AdminOnboarding>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            logError({ e.message.toString() })
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Retrieve an [APIResult] of type [AdminOnboarding] for `sso-connection` onboarding type
     *
     * @param orgDomain [String] of Organization's domain
     *
     * @see Cryptr.retrieveAdminOnboarding(orgDomain, "sso-connection")
     *
     * @return [APIResult] of type [AdminOnboarding]
     */
    fun retrieveSsoAdminOnboarding(orgDomain: String): APIResult<AdminOnboarding, ErrorMessage> {
        return retrieveAdminOnboarding(orgDomain, "sso-connection")
    }


    /**
     * Retrieve an Admin onboarding based on given parameters
     *
     * @param orgDomain [String] targeted Organization's domain
     * @param onboardingType [String] targeted onboarding type (ex: `sso-connection`)
     *
     * @return [APIResult] of type [AdminOnboarding]
     */
    fun retrieveAdminOnboarding(
        orgDomain: String,
        onboardingType: String
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val path = buildAdminOnboardingUrl(orgDomain, onboardingType)
        val response = makeRequest(path, apiKeyToken = retrieveApiKeyToken(), requestMethod = "GET")
        return try {
            APISuccess(format.decodeFromString<AdminOnboarding>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Invite IT Admin by email for the related [Organization] SsoAdminOnboarding
     *
     *  @param orgDomain [String] of the targeted Organization's domain
     *  @param itAdminEmail (Optional) [String] of the (new) IT admin email
     *
     *  @return [APIResult] of [AdminOnboarding]
     */

    fun inviteSsoAdminOnboarding(
        orgDomain: String,
        itAdminEmail: String? = null
    ): APIResult<AdminOnboarding, ErrorMessage> {
        return inviteAdminOnboarding(orgDomain, "sso-connection", itAdminEmail)
    }

    /**
     * Invite IT Admin by email for the related [Organization] AdminOnboarding
     *
     *  @param orgDomain [String] of the targeted Organization's domain
     *  @param onboardingType The type of targeted AdminOnboarding (ex: 'sso-connection')
     *  @param itAdminEmail (Optional) [String] of the (new) IT admin email
     *
     *  @return [APIResult] of [AdminOnboarding]
     */
    fun inviteAdminOnboarding(
        orgDomain: String,
        onboardingType: String,
        itAdminEmail: String? = null,
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val path = buildAdminOnboardingUrl(orgDomain, onboardingType) + "/invite"
        val params =
            if (itAdminEmail.isNullOrBlank()) mapOf<String, Any?>() else mapOf("it_admin_email" to itAdminEmail)

        val response = makeRequest(path, apiKeyToken = retrieveApiKeyToken(), requestMethod = "POST", params = params)

        return try {
            APISuccess(format.decodeFromString<AdminOnboarding>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Updates the[AdminOnboarding] for the given organization and providerType
     * @param orgDomain The domain of the targeted [Organization]
     * @param itAdminEmail the desired email address of the IT Admin able to handle the Onboarding
     * @param providerType the type of the provider (ex: Azure AD, OKTA...)
     * @param emailTemplateId the desired email template to use to send the [AdminOnboarding] to the Admin. If not
     * provided, default one will be used
     * @param sendEmail If you would like to send the email to the IT admin after update
     * @param applicationId the ClientID of the [Application] for the redirection
     *
     * @return an [APIResult] with the updated [AdminOnboarding]
     */
    fun updateSsoAdminOnboarding(
        orgDomain: String,
        itAdminEmail: String? = null,
        providerType: String? = null,
        emailTemplateId: String? = null,
        sendEmail: Boolean? = true,
        applicationId: String? = null,
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val customParams = mapOf(
            "provider_type" to providerType,
            "application_id" to applicationId
        )
        return updateAdminOnboarding(
            orgDomain, "sso-connection", itAdminEmail, emailTemplateId, sendEmail, customParams
        )
    }

    /**
     * Updates the[AdminOnboarding] for the given organization and providerType
     * @param orgDomain The domain of the targeted [Organization]
     * @param itAdminEmail the desired email address of the IT Admin able to handle the Onboarding
     * @param emailTemplateId the desired email template to use to send the [AdminOnboarding] to the Admin. If not
     * provided, default one will be used
     * @param sendEmail If you would like to send the email to the IT admin after update
     * @param customParams Map of any other desired parameter for the update
     *
     * @return an [APIResult] with the updated [AdminOnboarding]
     */
    fun updateAdminOnboarding(
        orgDomain: String,
        onboardingType: String,
        itAdminEmail: String? = null,
        emailTemplateId: String? = null,
        sendEmail: Boolean? = true,
        customParams: Map<String, Any?>? = mapOf<String, Any?>()
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val path = buildAdminOnboardingUrl(orgDomain, onboardingType)
        var basicParams: Map<String, Any?> = mapOf(
            "it_admin_email" to itAdminEmail,
            "email_template_id" to emailTemplateId,
            "send_email" to sendEmail,
            "onboarding_type" to onboardingType
        )
        val params = basicParams.toMutableMap()
        try {
            customParams?.map { params.put(it.key, it.value) }
        } catch (pe: Exception) {
            logException(pe)
            logError({ pe.message })
        }
        logDebug({ params.toString() })
        val response = makeRequest(
            path,
            serviceUrl = serviceUrl,
            params = params,
            apiKeyToken = retrieveApiKeyToken(),
            requestMethod = "PUT"
        )
        return try {
            APISuccess(format.decodeFromString<AdminOnboarding>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            logError({ e.message.toString() })
            APIError(ErrorMessage(response.toString()))
        }
    }

    /**
     * Reset SSO [AdminOnboarding] for the related [Organization]
     *
     *  @param orgDomain [String] of the targeted Organization's domain
     *
     *  @return [APIResult] of resetted [AdminOnboarding]
     */
    fun resetSsoAdminOnboarding(orgDomain: String): APIResult<AdminOnboarding, ErrorMessage> {
        return resetAdminOnboarding(orgDomain, "sso-connection")
    }

    /**
     * Reset [AdminOnboarding] for the related [Organization]
     *
     *  @param orgDomain [String] of the targeted Organization's domain
     *  @param onobardingType The type of AdminOnboarding to reset (ex: 'sso-connection')
     *
     *  @return [APIResult] of resetted [AdminOnboarding]
     */
    fun resetAdminOnboarding(
        orgDomain: String,
        onboardingType: String
    ): APIResult<AdminOnboarding, ErrorMessage> {
        val path = buildAdminOnboardingUrl(orgDomain, onboardingType) + "/reset"
        logDebug({ "resetAdminOnboarding" })
        val requestMethod = "PUT"
        logDebug({ requestMethod.toString() })
        val response = makeRequest(path, apiKeyToken = retrieveApiKeyToken(), requestMethod = requestMethod)
        return try {
            APISuccess(format.decodeFromString<AdminOnboarding>(response.toString()))
        } catch (e: Exception) {
            logException(e)
            APIError(ErrorMessage(response.toString()))
        }

    }

    /**
     * @suppress
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

    /**
     * Format to JSON String
     *
     * @param result the [APIResult] to format to JSON String
     *
     * @return JSON [String] depending on result is success or error
     */
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

    /**
     * Format  ChallengeResponse to JSON String
     *
     * @param result the [ChallengeResponse] to format to JSON String
     *
     * @return JSON [String] depending on provided ChallengeResponse
     */
    fun toJSONString(result: ChallengeResponse): String {
        return try {
            format.encodeToString(ChallengeResponse.serializer(), result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    /**
     * Format  ErrorMessage to JSON String
     *
     * @param result the [ErrorMessage] to format to JSON String
     *
     * @return JSON [String] depending on provided ErrorMessage
     *
     */
    fun toJSONString(result: ErrorMessage): String {
        return try {
            format.encodeToString(ErrorMessage.serializer(), result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    /**
     * Format the [CryptrResource] (User, AdminOnboarding, Organization...) to JSON String
     *
     * @param result the [CryptrResource] to format to JSON String
     *
     * @return JSON [String] depending on provided CryptrResource
     *
     */
    fun toJSONString(result: CryptrResource): String {
        return try {
            format.encodeToString(CryptrSerializer, result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    /**
     * Format the [DeletedResource] (DeletedOrganization...) to JSON String
     *
     * @param result the [DeletedResource] to format to JSON String
     *
     * @return JSON [String] depending on provided DeletedResource
     *
     */
    fun toJSONString(result: DeletedResource): String {
        return try {
            format.encodeToString(result)
        } catch (e: Exception) {
            logException(e)
            e.message.toString()
        }
    }

    /**
     * Formats the [PasswordChallengeResponse] to JSON string
     * @param result the [PasswordChallengeResponse] to format to JSON string
     *
     * @return JSON [String] encode of provided [PasswordChallengeResponse]
     */
    fun toJSONString(result: PasswordChallengeResponse): String {
        return try {
            format.encodeToString(result)
        } catch (e: Exception) {
            e.message.toString();
        }
    }
}