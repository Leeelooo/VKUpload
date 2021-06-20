package com.leeloo.vkupload.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leeloo.vkupload.ui.ModelState
import com.leeloo.vkupload.utils.getFileSize
import com.leeloo.vkupload.vo.VKVideoUpload
import java.util.*

interface VideoRepository {
    val liveData: LiveData<ModelState>
    fun getVideos()
    fun createNewEntry(uri: Uri, title: String, sessionUUID: UUID)
    fun updateEntryTransferredSize(id: Long, transferredSize: Long)
    fun deleteEntry(id: Long)
    fun deleteAll()
    fun closeConnections()

    companion object {
        private lateinit var _INSTANCE: VideoRepository

        fun createInstance(context: Context) {
            _INSTANCE = VideoRepositoryImpl(context)
        }

        fun getInstance(): VideoRepository = _INSTANCE

    }
}

class VideoRepositoryImpl(
    private val context: Context
) : VideoRepository {
    private val _liveData = MutableLiveData<ModelState>()
    override val liveData: LiveData<ModelState>
        get() = _liveData

    private val dbHelper = VideoDBHelper(context).also { it.writableDatabase }

    override fun getVideos() {
        _liveData.value = ModelState.VideosLoading
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            VideoContract.VideoEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        try {
            val videos = mutableListOf<VKVideoUpload>()
            if (cursor.moveToNext()) {
                do {
                    videos.add(cursor.getVideo())
                } while (cursor.moveToNext())
            }

            _liveData.value = ModelState.VideosLoaded(videos)
        } catch (e: Exception) {
            _liveData.value = ModelState.VideosLoadingFailed(e)
        } finally {
            cursor.close()
        }

    }

    override fun createNewEntry(uri: Uri, title: String, sessionUUID: UUID) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(VideoContract.VideoEntry.COLUMN_NAME_TITLE, title)
            put(VideoContract.VideoEntry.COLUMN_NAME_URI, uri.toString())
            put(VideoContract.VideoEntry.COLUMN_NAME_TOTAL_SIZE, getFileSize(context, uri))
            put(VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE, 0L)
            put(VideoContract.VideoEntry.COLUMN_NAME_SESSION_UUID, sessionUUID.toString())
        }
        val videoId = db.insert(VideoContract.VideoEntry.TABLE_NAME, null, values)

        val video = getVideo(videoId)
        if (video != null) {
            _liveData.postValue(ModelState.VideoUploadingStart(video))
        }
    }

    override fun updateEntryTransferredSize(id: Long, transferredSize: Long) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE, transferredSize)
        }
        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val updatedRows =
            db.update(VideoContract.VideoEntry.TABLE_NAME, values, selection, arrayOf("$id"))

        val video = getVideo(id)
        if (updatedRows == 1 && video != null) {
            _liveData.postValue(ModelState.VideoUpdated(video))
        }
    }

    override fun deleteEntry(id: Long) {
        val db = dbHelper.writableDatabase
        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val deletedRows = db.delete(VideoContract.VideoEntry.TABLE_NAME, selection, arrayOf("$id"))

        if (deletedRows == 1) {
            _liveData.postValue(ModelState.DeleteVideo(id))
        }
    }

    override fun deleteAll() {
        dbHelper.writableDatabase
            .rawQuery(VideoContract.SQL_DELETE_ENTRIES, null)
            .close()
    }

    override fun closeConnections() {
        dbHelper.close()
    }

    private fun getVideo(id: Long): VKVideoUpload? {
        val db = dbHelper.readableDatabase

        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val cursor = db.query(
            VideoContract.VideoEntry.TABLE_NAME,
            projection,
            selection,
            arrayOf("$id"),
            null,
            null,
            null
        )

        try {
            if (cursor.moveToNext()) {
                return cursor.getVideo()
            }
            return null
        } catch (e: Exception) {
            return null
        } finally {
            cursor.close()
        }
    }

    companion object {
        private val projection = arrayOf(
            VideoContract.VideoEntry.COLUMN_NAME_ID,
            VideoContract.VideoEntry.COLUMN_NAME_TITLE,
            VideoContract.VideoEntry.COLUMN_NAME_URI,
            VideoContract.VideoEntry.COLUMN_NAME_TOTAL_SIZE,
            VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE,
            VideoContract.VideoEntry.COLUMN_NAME_SESSION_UUID
        )

        private fun Cursor.getVideo() =
            VKVideoUpload(
                id = getLong(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_ID)),
                uri = getString(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_URI)),
                title = getString(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_TITLE)),
                totalSize = getLong(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_TOTAL_SIZE)),
                transferredSize = getLong(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE)),
                sessionUUID = UUID.fromString(
                    getString(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_SESSION_UUID))
                )
            )

    }

}
