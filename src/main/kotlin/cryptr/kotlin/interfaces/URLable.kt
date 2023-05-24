package cryptr.kotlin.interfaces

import cryptr.kotlin.models.Application
import cryptr.kotlin.models.Organization
import cryptr.kotlin.models.User
import cryptr.kotlin.objects.Constants
import java.net.URLEncoder

/**
 * Interface to build Cryptr URLs
 */
interface URLable {

    /**
     * Basic helper to build Cryptr URL
     *
     * @param serviceUrl The Cryptr service base URL
     * @param path The path to append to serviceUrl
     *
     * @return the concatenated URL [String]
     */
    fun buildCryptrUrl(serviceUrl: String, path: String): String {
        return if (path.startsWith("/")) "$serviceUrl$path" else "$serviceUrl/$path"
    }

    /**
     * Build for formData output using [Map] input
     *
     * @param params Map<String, Any?> To parse to formData
     * @param prepend Optional string to format params to form data
     *
     * @return the formData [String]
     */
    fun mapToFormData(params: Map<String, Any?>, prepend: String? = null): String {
        return params
            .entries
            .stream()
            .filter { it.value != null }
            .map { (key, value) ->
                val realKey = if (prepend !== null) "$prepend[$key]" else key
                when (value) {
                    is ArrayList<*> ->
                        value.joinToString(separator = "&") {
                            "$realKey[]=" + URLEncoder.encode(
                                it.toString(),
                                "utf-8"
                            )
                        }

                    is Map<*, *> ->
                        mapToFormData(value.entries.associate { it.key.toString() to it.value }, "$key")

                    else ->
                        "$realKey=" + URLEncoder.encode(value.toString(), "utf-8")
                }
            }
            .reduce { p1, p2 -> "$p1&$p2" }
            .map { s -> s }
            .orElse("")
    }

    /**
     * @suppress
     */
    fun buildApiPath(resourceName: String, resourceId: String? = null): String {
        val baseApiPath = Constants.API_BASE_BATH + "/" + Constants.API_VERSION + "/" + resourceName
        return if (resourceId != null && resourceId.length > 0) "$baseApiPath/$resourceId" else baseApiPath
    }

    /**
     * @suppress
     */
    fun buildOrganizationPath(resourceId: String? = null): String {
        return buildApiPath(Organization.apiResourceName, resourceId)
    }

    /**
     * @suppress
     */
    fun buildOrganizationResourcePath(
        orgDomain: String,
        resourceName: String,
        resourceId: String? = null
    ): String {
        val baseApiOrgResourcePath =
            Constants.API_BASE_BATH + "/" + Constants.API_VERSION + "/org/" + orgDomain + "/" + resourceName
        return if (resourceId !== null && resourceId.isNotEmpty()) "$baseApiOrgResourcePath/$resourceId" else baseApiOrgResourcePath
    }

    /**
     * @suppress
     */
    fun buildUserPath(orgDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(orgDomain, User.apiResourceName, resourceId)
    }

    /**
     * @suppress
     */
    fun buildApplicationPath(orgDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(orgDomain, Application.apiResourceName, resourceId)
    }

    /**
     * @suppress
     */
    fun buildSsoConnectionPath(orgDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(orgDomain, "sso-connections", resourceId)
    }

    /**
     * @suppress
     */
    fun buildAdminOnboardingUrl(orgDomain: String, onboardingType: String): String {
        return buildOrganizationResourcePath(orgDomain, "admin-onboarding", onboardingType)
    }
}