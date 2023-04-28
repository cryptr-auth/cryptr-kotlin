package cryptr.kotlin

import cryptr.kotlin.enums.ChallengeType
import cryptr.kotlin.enums.Environment
import org.json.JSONObject

/**
 * Cryptr for headless process
 *
 * With this class you can initiate and consume headless challenges :
 * - SSO SAML Challenges
 */
class CryptrHeadless(
    tenantDomain: String = System.getProperty(Environment.CRYPTR_TENANT_DOMAIN.toString()),
    baseUrl: String = System.getProperty(Environment.CRYPTR_BASE_URL.toString(), DEFAULT_BASE_URL),
    defaultRedirectUrl: String = System.getProperty(
        Environment.CRYPTR_DEFAULT_REDIRECT_URL.toString(),
        DEFAULT_REDIRECT_URL
    ),
    apiKeyClientId: String = System.getProperty(Environment.CRYPTR_API_KEY_CLIENT_ID.toString()),
    apiKeyClientSecret: String = System.getProperty(Environment.CRYPTR_API_KEY_CLIENT_SECRET.toString())
) :
    Cryptr(tenantDomain, baseUrl, defaultRedirectUrl, apiKeyClientId, apiKeyClientSecret) {

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
    ): JSONObject? {
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
    ): JSONObject? {
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
     * @return a JSONObject with `authorization_url`that end-user has to open to do his authententication process
     */
    fun createSSOChallenge(
        redirectUri: String = defaultRedirectUrl,
        orgDomain: String? = null,
        userEmail: String? = null,
        authType: ChallengeType? = ChallengeType.SAML
    ): JSONObject? {
        if (orgDomain != null || userEmail != null) {
            val path = "api/v2/sso-${authType?.value}-challenges"
            val params = if (orgDomain !== null) mapOf(
                "redirect_uri" to redirectUri,
                "org_domain" to orgDomain
            ) else mapOf("redirect_uri" to redirectUri, "user_email" to userEmail)
            return makeRequest(path, params, retrieveApiKeyToken())
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
            return makeRequest("oauth/token", params, retrieveApiKeyToken())
        } else {
            throw Exception("code is required")
        }
    }
}