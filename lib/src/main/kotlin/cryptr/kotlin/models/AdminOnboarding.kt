package cryptr.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AdminOnboarding")
data class AdminOnboarding(
    @SerialName("__type__") override val cryptrType: String = "",
    @SerialName("__environment__") override val environment: String? = null,
    @SerialName("__domain__") override val resourceDomain: String? = null,

    @SerialName("id") val id: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("tutorial_step") val tutorialStep: Int = 0,
    @SerialName("email_template_id") val emailTemplateId: String? = null,
    @SerialName("provider_type") val providerType: String? = null,
    @SerialName("sso_admin_email") val ssoAdminEmail: String,
    @SerialName("onboarding_type") val onboardingType: String = "sso-connection",
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("inserted_at") val insertedAt: String? = null,
) : CryptrResource()
