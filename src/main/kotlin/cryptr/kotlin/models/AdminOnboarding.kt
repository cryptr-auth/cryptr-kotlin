package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation of the onboarding for an Admin to configure for onboardingType
 */
@Serializable
@SerialName("AdminOnboarding")
data class AdminOnboarding(
    @SerialName("__type__") override val cryptrType: String = "",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    /**
     * The unique identifier of the Onboarding
     */
    @SerialName("id") val id: String? = null,
    /**
     * State reached by the Admin on his onboarding progression. Ex: `provider_type_chosen`
     */
    @SerialName("state") val state: String? = null,
    /**
     * Tutorial step reached by the Admin
     */
    @SerialName("tutorial_step") val tutorialStep: Int = 0,
    /**
     * Email template unique identifier used to contact the IT admin
     */
    @SerialName("email_template_id") val emailTemplateId: String? = null,
    /**
     * Chosen provider type (for SSO connection) . Ex: okta, google ...
     */
    @SerialName("provider_type") val providerType: String? = null,
    /**
     * Email of the IT Admin
     */
    @SerialName("it_admin_email") val itAdminEmail: String,
    /**
     * Type of onboarding. Default `sso-connection`
     */
    @SerialName("onboarding_type") val onboardingType: String = "sso-connection",
    /**
     * [String] Date of last update
     */
    @SerialName("updated_at") val updatedAt: String? = null,
    /**
     * [String] Date of creation
     */
    @SerialName("inserted_at") val insertedAt: String? = null,
    /**
     * [String] Date of last invitation sending
     */
    @SerialName("invitation_sent_at") val invitationSentAt: String? = null,
    /**
     * [Organization] to which the Onboarding is linked to
     */
    @SerialName("organization") val organization: Organization? = null,
    /**
     * @see onboardingType
     */
    @SerialName("for") val forOnboardingType: String? = null
) : CryptrResource()
