package cryptr.kotlin.interfaces

import java.net.URLEncoder

interface URLable {

    fun buildCryptrUrl(baseUrl: String, path: String): String {
        return if (path.startsWith("/")) "$baseUrl$path" else "$baseUrl/$path"
    }

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
}