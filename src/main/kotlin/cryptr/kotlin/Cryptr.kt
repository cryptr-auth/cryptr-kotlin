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
        CryptrEnvironment.CRYPTR_SERVICE_URL.toString(),
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
    val formatNoNulNoDefaults = Json { ignoreUnknownKeys = true; explicitNulls = false; encodeDefaults = false }

    @OptIn(ExperimentalSerializationApi::class)
    val format = Json { ignoreUnknownKeys = true; explicitNulls = true; encodeDefaults = true }
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
                APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            return APIError(ErrorMessage.build("password challenge code missing"))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
                APIError(handleAPIException(e, response))
            }
        } else {
            return APIError(ErrorMessage.build("code is required"))
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
                APIError(handleAPIException(e, response))
            }
        } else {
            return APIError(ErrorMessage.build("code is required"))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
        allowedEmailDomains: Set<String>
    ): APIResult<Organization, ErrorMessage> {
        return createOrganization(Organization(name = name, allowedEmailDomains = allowedEmailDomains))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
                APIError(ErrorMessage.build(response.toString()))
            }
        } catch (e: Exception) {
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
            APIError(handleAPIException(e, response))
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
     * Format the [CryptrResource] (User, Organization...) to JSON String
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

    private fun handleAPIException(e: Exception, response: JSONObject): ErrorMessage {
        return try {
            logException(e)
            format.decodeFromString<ErrorMessage>(response.toString())
        } catch (e1: Exception) {
            logException(e1)
            val errorContent = ErrorContent(message = response.toString())
            ErrorMessage(error = errorContent)
        }
    }
}