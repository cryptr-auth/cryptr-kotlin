package cryptr.kotlin

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
     * Generate a SSO SAMl Challenge according to authentication and given parameters
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
        if (orgDomain != null || userEmail != null) {
            val params = if (orgDomain !== null) mapOf(
                "redirect_uri" to redirectUri,
                "org_domain" to orgDomain
            ) else mapOf("redirect_uri" to redirectUri, "user_email" to userEmail)
            return makeRequest("api/v2/sso-saml-challenges", params, retrieveApiKeyToken())
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