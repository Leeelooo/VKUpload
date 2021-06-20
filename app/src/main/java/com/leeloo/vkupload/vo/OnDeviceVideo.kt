package com.leeloo.vkupload.vo

import android.net.Uri

data class OnDeviceVideo(
    val uri: Uri,
    val size: Long,
    val title: String
)