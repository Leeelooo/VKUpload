package com.leeloo.vkupload.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.leeloo.vkupload.vo.LocalVideoUpload
import com.leeloo.vkupload.vo.RemoteVideoPost
import java.util.*

interface VideoUploadRepository {
    fun getCurrentUploadTasks(): List<LocalVideoUpload>
    fun createTask(remoteVideoPost: RemoteVideoPost, localVideoId: Long): LocalVideoUpload?
    fun deleteUploadTask(id: Long)

    companion object {
        private lateinit var _INSTANCE: VideoUploadRepository

        fun createInstance(context: Context) {
            _INSTANCE = VideoUploadRepositoryImpl(context)
        }

        fun getInstance(): VideoUploadRepository = _INSTANCE

    }
}

private class VideoUploadRepositoryImpl(
    context: Context
) : VideoUploadRepository {
    private val dbHelper = VideoDBHelper(context)

    override fun getCurrentUploadTasks(): List<LocalVideoUpload> =
        dbHelper.readableDatabase.query(
            VideoContract.VideoUploadEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        ).use {
            val uploadTasks = mutableListOf<LocalVideoUpload>()
            while (it.moveToNext()) {
                uploadTasks.add(it.getVideoUploadTask())
            }
            return@use uploadTasks
        }

    override fun createTask(
        remoteVideoPost: RemoteVideoPost,
        localVideoId: Long
    ): LocalVideoUpload? {
        val values = ContentValues().apply {
            put(VideoContract.VideoUploadEntry.COLUMN_NAME_ACCESS_KEY, remoteVideoPost.accessKey)
            put(VideoContract.VideoUploadEntry.COLUMN_NAME_LOCAL_VIDEO_ID, localVideoId)
            put(
                VideoContract.VideoUploadEntry.COLUMN_NAME_SESSION_UUID,
                UUID.randomUUID().toString()
            )
            put(VideoContract.VideoUploadEntry.COLUMN_NAME_REMOTE_VIDEO_ID, remoteVideoPost.videoId)
            put(VideoContract.VideoUploadEntry.COLUMN_NAME_UPLOAD_URL, remoteVideoPost.uploadUrl)
        }

        val createdUploadTaskId = dbHelper.writableDatabase
            .insert(VideoContract.VideoUploadEntry.TABLE_NAME, null, values)
        return getUploadTask(createdUploadTaskId)
    }

    override fun deleteUploadTask(id: Long) {
        val selection = "${VideoContract.VideoUploadEntry.COLUMN_NAME_ID} = ?"
        dbHelper.writableDatabase
            .delete(VideoContract.VideoUploadEntry.TABLE_NAME, selection, arrayOf("$id"))
    }

    private fun getUploadTask(id: Long): LocalVideoUpload? =
        dbHelper.readableDatabase.query(
            VideoContract.VideoUploadEntry.TABLE_NAME,
            null,
            "${VideoContract.VideoUploadEntry.COLUMN_NAME_ID} = ?",
            arrayOf("$id"),
            null,
            null,
            null
        ).use {
            if (it.moveToNext()) {
                return@use it.getVideoUploadTask()
            }
            return@use null
        }

    companion object {
        private fun Cursor.getVideoUploadTask(): LocalVideoUpload =
            LocalVideoUpload(
                id = this.getLong(
                    this.getColumnIndexOrThrow(
                        VideoContract.VideoUploadEntry.COLUMN_NAME_ID
                    )
                ),
                accessKey = this.getString(
                    this.getColumnIndexOrThrow(
                        VideoContract.VideoUploadEntry.COLUMN_NAME_ACCESS_KEY
                    )
                ),
                uploadUrl = this.getString(
                    this.getColumnIndexOrThrow(
                        VideoContract.VideoUploadEntry.COLUMN_NAME_UPLOAD_URL
                    )
                ),
                videoId = this.getLong(
                    this.getColumnIndexOrThrow(
                        VideoContract.VideoUploadEntry.COLUMN_NAME_REMOTE_VIDEO_ID
                    )
                ),
                sessionUUID = this.getString(
                    this.getColumnIndexOrThrow(
                        VideoContract.VideoUploadEntry.COLUMN_NAME_SESSION_UUID
                    )
                ),
                inAppVideoId = this.getLong(
                    this.getColumnIndexOrThrow(
                        VideoContract.VideoUploadEntry.COLUMN_NAME_LOCAL_VIDEO_ID
                    )
                )
            )
    }

}