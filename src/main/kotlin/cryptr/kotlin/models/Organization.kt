package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent one of your customer company
 */
@Serializable
data class Organization(
    @SerialName("__type__")
    override val cryptrType: String = "Organization",
    /**
     * No value for [Organization]
     */
    @SerialName("__environment__")
    override val environment: String? = null,
    /**
     * Unique identifier created from the slugifierd name
     */
    val domain: String? = null,
    /**
     * Last update date
     */
    @SerialName("updated_at")
    val updatedAt: String? = null,
    /**
     * Name of your customer company
     */
    val name: String,
    /**
     * Date of creation
     */
    @SerialName("inserted_at")
    val insertedAt: String? = null,
    /**
     * List of [Environment] (aka databases) for the Organization
     */
    val environments: Set<Environment> = setOf(),
    /**
     * List of email domains that can be used to connect through this organization authentication methods
     */
    @SerialName("allowed_email_domains")
    val allowedEmailDomains: Set<String>? = setOf(),
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "organizations"
    }
}
