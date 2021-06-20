package com.leeloo.vkupload.requests

import com.leeloo.vkupload.vo.RemoteUser
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class GetUserRequest(
    userIds: IntArray,
    fields: String
) : VKRequest<RemoteUser>("users.get") {

    init {
        if (userIds.isNotEmpty()) {
            addParam("user_ids", userIds)
        }
        addParam("fields", fields)
    }

    override fun parse(r: JSONObject) = RemoteUser.parse(r)

}