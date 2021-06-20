package com.leeloo.vkupload.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.leeloo.vkupload.vo.OnDeviceVideo

private enum class Columns {
    COLUMN_VIDEO_SIZE,
    COLUMN_VIDEO_TITLE
}

private val requiredFields = mapOf(
    Columns.COLUMN_VIDEO_SIZE to MediaStore.MediaColumns.SIZE,
    Columns.COLUMN_VIDEO_TITLE to MediaStore.MediaColumns.DISPLAY_NAME
)

fun getVideo(context: Context, uri: Uri): OnDeviceVideo {
    val cursor = getFileCursor(context, uri)
    return cursor.use {
        cursor?.moveToNext()
        OnDeviceVideo(
            uri = uri,
            size = cursor?.getLong(cursor.getColumnIndexOrThrow(requiredFields[Columns.COLUMN_VIDEO_SIZE]))
                ?: 0L,
            title = cursor?.getString(cursor.getColumnIndexOrThrow(requiredFields[Columns.COLUMN_VIDEO_TITLE]))
                ?: ""
        )
    }
}

private fun getFileCursor(context: Context, uri: Uri) =
    context.contentResolver.query(
        uri,
        requiredFields.values.toTypedArray(),
        null,
        null,
        null
    )

