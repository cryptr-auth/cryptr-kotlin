package cryptr.kotlin.enums

import cryptr.kotlin.models.Application
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Language type of [Application]
 */
@Serializable
enum class ApplicationType(val type: String) {
    @SerialName("android")
    ANDROID("android"),

    @SerialName("angular")
    ANGULAR("angular"),

    @SerialName("ios")
    IOS("ios"),

    @SerialName("java_app")
    JAVA_APP("java_app"),

    @SerialName("java_spring")
    JAVA_SPRING("java_spring"),

    @SerialName("mobile")
    MOBILE("mobile"),

    @SerialName("node")
    NODE("node"),

    @SerialName("node_express")
    NODE_EXPRESS("node_express"),

    @SerialName("react")
    REACT("react"),

    @SerialName("php")
    PHP("php"),

    @SerialName("php_laravel")
    PHP_LARAVEL("php_laravel"),

    @SerialName("php_symfony")
    PHP_SYMFONY("php_symfony"),

    @SerialName("python_flask")
    PYTHON_FLASK("python_flask"),

    @SerialName("regular_web")
    REGULAR_WEB("regular_web"),

    @SerialName("ruby")
    RUBY("ruby"),

    @SerialName("ruby_on_rails")
    RUBY_ON_RAILS("ruby_on_rails"),

    @SerialName("spa")
    SPA("spa"),

    @SerialName("vue")
    VUE("vue"),

    @SerialName("web")
    WEB("web"),

    @SerialName("webflow")
    WEBFLOW("webflow"),
}