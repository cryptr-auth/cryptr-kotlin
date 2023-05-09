package cryptr.kotlin

import cryptr.kotlin.models.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

object CryptrSerializer : JsonContentPolymorphicSerializer<CryptrResource>(CryptrResource::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<CryptrResource> {
        val jsonObjElement = JSONObject(element.toString())
        if (jsonObjElement.has("data") && jsonObjElement.optJSONArray("data") !== null) {
            val itemType = jsonObjElement.getJSONArray("data").getJSONObject(0).optString("__type__")
            val listSerializerType = listItemSerializer(itemType)
            return Listing.serializer(listSerializerType)
        }

        val rootType = element.jsonObject["__type__"]?.jsonPrimitive?.content
        println("rootType $rootType")
        return listItemSerializer(rootType)
    }

    private fun listItemSerializer(itemType: String?): KSerializer<out CryptrResource> {
        println("here looking for $itemType")
        return when (itemType) {
            "Address" -> Address.serializer()
            "Application" -> Application.serializer()
            "Organization" -> Organization.serializer()
            "Profile" -> Profile.serializer()
            "User" -> User.serializer()
            else -> throw Exception("Error list serializer not found for $itemType")
        }
    }
}