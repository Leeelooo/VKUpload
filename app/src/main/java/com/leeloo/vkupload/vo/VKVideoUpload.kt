package com.leeloo.vkupload.vo

import java.util.*

data class VKVideoUpload(
    val id: Long,
    val uri: String,
    val title: String,
    val sessionUUID: UUID,
    val totalSize: Long,
    val transferredSize: Long
)