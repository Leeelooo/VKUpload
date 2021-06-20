package com.leeloo.vkupload.vo

data class LocalVideoUpload(
    val accessKey: String,
    val uploadUrl: String,
    val videoId: Long,
    val sessionUUID: String,
    val inAppVideoId: Long
)