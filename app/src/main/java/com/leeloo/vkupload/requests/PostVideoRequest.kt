package com.leeloo.vkupload.requests

import com.leeloo.vkupload.vo.RemoteVideoPost
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class PostVideoRequest(
    name: String,
    description: String = "",
    isPrivate: Int = 0,
    wallpost: Int? = null,
    link: String? = null,
    groupId: Int? = null,
    albumId: Int? = null,
    privacyView: Array<String> = emptyArray(),
    privacyComment: Array<String> = emptyArray(),
    noComments: Int = 0,
    repeat: Int = 0,
    compression: Int = 0
) : VKRequest<RemoteVideoPost>("video.save") {

    init {
        addParam("name", name)
        addParam("description", description)
        addParam("is_private", isPrivate)
        if (wallpost != null) {
            addParam("wallpost", wallpost)
        }
        if (link != null) {
            addParam("link", link)
        }
        if (groupId != null) {
            addParam("group_id", groupId)
        }
        if (albumId != null) {
            addParam("album_id", albumId)
        }
        if (privacyView.isNotEmpty()) {
            addParam("privacy_view", privacyView)
        }
        if (privacyComment.isNotEmpty()) {
            addParam("privacy_comment", privacyComment)
        }
        addParam("no_comments", noComments)
        addParam("repeat", repeat)
        addParam("compression", compression)
    }

    override fun parse(r: JSONObject): RemoteVideoPost =
        RemoteVideoPost.parse(r)
}