package com.leeloo.vkupload.vo

import org.json.JSONObject

data class RemoteVideoPost(
    val accessKey: String,
    val uploadUrl: String,
    val videoId: Long
) {

    companion object {
        fun parse(jsonObject: JSONObject): RemoteVideoPost =
            RemoteVideoPost(
                accessKey = jsonObject.getString("access_key"),
                uploadUrl = jsonObject.getString("upload_url"),
                videoId = jsonObject.getLong("video_id")
            )
    }

}