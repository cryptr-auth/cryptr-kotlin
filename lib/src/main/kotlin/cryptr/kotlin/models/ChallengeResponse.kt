package cryptr.kotlin.models

import org.json.JSONObject

data class ChallengeResponse(val accessToken: String?) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("access_token")
    )
}
