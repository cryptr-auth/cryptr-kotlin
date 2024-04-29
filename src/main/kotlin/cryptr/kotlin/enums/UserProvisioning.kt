package cryptr.kotlin.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserProvisioning {
    /**
     * Users can connect but they are not registered
     */
    @SerialName("unregistered_users_allowed")
    AUTHORIZE_USERS_WITH_NO_PROVISION,

    /**
     * Users coming from IDP has to be registered before logging
     */
    @SerialName("only_registered_users")
    ONLY_REGISTERED_USERS,

    /**
     * Users are provisioned as they come from IDP
     */
    @SerialName("provision_new_users")
    PROVISION_NEW_USERS,
}