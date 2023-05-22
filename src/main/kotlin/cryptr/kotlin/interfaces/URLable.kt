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
     * @param baseUrl The Cryptr service base URL
     * @param path The path to append to baseUrl
     *
     * @return the concatenated URL [String]
     */
    fun buildCryptrUrl(baseUrl: String, path: String): String {
        return if (path.startsWith("/")) "$baseUrl$path" else "$baseUrl/$path"
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
        organizationDomain: String,
        resourceName: String,
        resourceId: String?
    ): String {
        val baseApiOrgResourcePath =
            Constants.API_BASE_BATH + "/" + Constants.API_VERSION + "/org/" + organizationDomain + "/" + resourceName
        return if (resourceId !== null && resourceId.isNotEmpty()) "$baseApiOrgResourcePath/$resourceId" else baseApiOrgResourcePath
    }

    /**
     * @suppress
     */
    fun buildUserPath(organizationDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(organizationDomain, User.apiResourceName, resourceId)
    }

    /**
     * @suppress
     */
    fun buildApplicationPath(organizationDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(organizationDomain, Application.apiResourceName, resourceId)
    }

    /**
     * @suppress
     */
    fun buildSSOConnectionPath(organizationDomain: String, resourceId: String? = null): String {
        return buildOrganizationResourcePath(organizationDomain, "sso-connections", resourceId)
    }

    /**
     * @suppress
     */
    fun buildAdminOnboardingUrl(organizationDomain: String, onboardingType: String): String {
        return buildOrganizationResourcePath(organizationDomain, "admin-onboarding", onboardingType)
    }
}