package cryptr.kotlin.models

import cryptr.kotlin.enums.OrganizationStatusType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationStatus(
    /**
     * Possible encountered errors
     */
    val errors: Set<String> = emptySet(),
    /**
     * Estimated seconds remaining to complete [Organization] creation
     */
    @SerialName("estimated_time_to_complete_in_seconds") val estimatedTimeToCompleteInSeconds: Long? = null,
    /**
     * Progress in percentage while creating [Organization] . null if terminated
     */
    @SerialName("progress_in_percentage") val progressInPercentage: Long? = null,
    /**
     * If [Organization] is ready or not
     */
    val state: OrganizationStatusType
)

/**
 * Represent one of your customer company
 */
@Serializable
data class Organization(
    @SerialName("__type__")
    override val cryptrType: String = "Organization",
    /**
     * Unique identifier created from the slugifierd name
     */
    val domain: String? = null,
    /**
     * Last update date
     */
    /**
     * Color when listed in Dashboard
     */
    val color: String? = null,
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

    /**
     * Icon logo for the [Organization]
     */
    @SerialName("icon_logo_url") val iconLogoUrl: String? = null,
    /**
     * Inline logo for the [Organization]
     */
    @SerialName("inline_logo_url") val inlineLogoUrl: String? = null,
    /**
     * Locale
     */
    val locale: String? = null,
    /**
     * Organization timezone
     *
     * ex: Coordinated Universal Time (UTC)
     * ex: Central European Time (CET)
     */
    val timezone: String? = null,
    /**
     * current status of [Organization]
     */
    val status: OrganizationStatus? = null
) : CryptrResource() {
    companion object {
        const val apiResourceName: String = "organizations"
    }
}
