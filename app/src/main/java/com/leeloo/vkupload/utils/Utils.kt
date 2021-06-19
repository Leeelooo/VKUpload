package com.leeloo.vkupload.utils

import android.content.Context
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Size
import java.io.File
import java.io.FileOutputStream

fun getPath(context: Context, uri: Uri): Uri {
    val inputStream = context.contentResolver.openInputStream(uri)
    val cacheFile = File(context.cacheDir, "")
    val outputStream = FileOutputStream(cacheFile)
    inputStream?.copyTo(outputStream)
    return Uri.parse("file://" + cacheFile.path)
}

fun getSize(context: Context, uri: Uri): Long =
    context.contentResolver
        .query(uri, null, null, null, null)
        ?.use { it.getLong(it.getColumnIndex(OpenableColumns.SIZE)) } ?: 0L