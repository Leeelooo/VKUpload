package com.leeloo.vkupload.vo

import org.json.JSONObject

data class VKUser(
    val photoURL: String
) {
    companion object {
        fun parse(r: JSONObject): VKUser =
            VKUser(r.getJSONArray("response").getJSONObject(0).optString("photo_200"))
    }
}