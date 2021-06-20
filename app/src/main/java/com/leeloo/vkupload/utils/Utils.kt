package com.leeloo.vkupload.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.leeloo.vkupload.R

fun Long.formatFileSize(context: Context): String {
    var times = 0
    var currentSize = this.toDouble()
    while (currentSize / 1024 > 1.0 && times < 3) {
        currentSize /= 1024
        times++
    }
    return context.resources.getString(
        when (times) {
            0 -> R.string.filesize_bytes
            1 -> R.string.filesize_kilobytes
            2 -> R.string.filesize_megabytes
            else -> R.string.filesize_gigabytes
        },
        currentSize
    )
}