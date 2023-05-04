package cryptr.kotlin.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ApplicationType(val type: String) {
    @SerialName("angular")
    ANGULAR("angular"),

    @SerialName("react")
    REACT("react"),

    @SerialName("regular_web")
    REGULAR_WEB("regular_web"),

    @SerialName("vue")
    VUE("vue"),
    
    @SerialName("ruby_on_rails")
    RUBY_ON_RAILS("ruby_on_rails")
}