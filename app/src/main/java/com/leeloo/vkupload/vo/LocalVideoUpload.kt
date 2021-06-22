package com.leeloo.vkupload.vo

data class LocalVideoUpload(
    val id: Long,
    val accessKey: String,
    val uploadUrl: String,
    val videoId: Long,
    val sessionUUID: String,
    val inAppVideoId: Long
)