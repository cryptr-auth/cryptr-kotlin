package cryptr.kotlin.enums


/**
 * Environment keys to setup Cryptr SDK instance
 */
enum class CryptrEnvironment {
    /**
     * Key to store API Key Client ID
     */
    CRYPTR_API_KEY_CLIENT_ID,

    /**
     * Key to store API Key Client SECRET
     */
    CRYPTR_API_KEY_CLIENT_SECRET,

    /**
     * Key to store Cryptr Service URL
     */
    CRYPTR_BASE_URL,

    /**
     * Key to store your JVM default Redirect URL
     */
    CRYPTR_DEFAULT_REDIRECT_URL,

    /**
     * Key to store your Account tenant domain
     */
    CRYPTR_ACCOUNT_DOMAIN
}
