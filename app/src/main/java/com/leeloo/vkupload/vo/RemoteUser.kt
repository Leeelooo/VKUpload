package com.leeloo.vkupload.vo

import org.json.JSONObject

data class RemoteUser(
    val photoURL: String
) {
    companion object {
        fun parse(r: JSONObject): RemoteUser =
            RemoteUser(r.getJSONArray("response").getJSONObject(0).optString("photo_200"))
    }
}