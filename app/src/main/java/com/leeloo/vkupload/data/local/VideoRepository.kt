package com.leeloo.vkupload.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leeloo.vkupload.ui.ModelState
import com.leeloo.vkupload.vo.LocalVideo
import com.leeloo.vkupload.vo.OnDeviceVideo
import com.leeloo.vkupload.vo.VideoUploadStatus
import kotlin.concurrent.thread

interface VideoRepository {
    val liveData: LiveData<ModelState>
    fun getVideos()
    fun getVideo(id: Long): LocalVideo?
    fun createNewEntry(onDeviceVideo: OnDeviceVideo, title: String)
    fun updateEntryTransferredSize(id: Long, transferredSize: Long)
    fun updateEntryStatus(id: Long, newState: VideoUploadStatus)
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

private class VideoRepositoryImpl(
    context: Context
) : VideoRepository {
    private val _liveData = MutableLiveData<ModelState>()
    override val liveData: LiveData<ModelState>
        get() = _liveData

    private val dbHelper = VideoDBHelper(context)

    override fun getVideos() {
        _liveData.value = ModelState.VideosLoading
        thread {
            dbHelper.readableDatabase.query(
                VideoContract.VideoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            ).use {
                try {
                    val videos = mutableListOf<LocalVideo>()
                    if (it.moveToNext()) {
                        do {
                            videos.add(it.getVideo())
                        } while (it.moveToNext())
                    }

                    _liveData.postValue(ModelState.VideosLoaded(videos))
                } catch (e: Exception) {
                    _liveData.postValue(ModelState.VideosLoadingFailed(e))
                }
            }
        }
    }

    override fun getVideo(id: Long): LocalVideo? {
        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val cursor = dbHelper.readableDatabase.query(
            VideoContract.VideoEntry.TABLE_NAME,
            null,
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

    override fun createNewEntry(onDeviceVideo: OnDeviceVideo, title: String) {
        val values = ContentValues().apply {
            put(VideoContract.VideoEntry.COLUMN_NAME_TITLE, title)
            put(VideoContract.VideoEntry.COLUMN_NAME_URI, onDeviceVideo.uri.toString())
            put(VideoContract.VideoEntry.COLUMN_NAME_TOTAL_SIZE, onDeviceVideo.size)
            put(VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE, 0L)
            put(VideoContract.VideoEntry.COLUMN_NAME_STATUS, VideoUploadStatus.PENDING.ordinal)
        }
        thread {
            val videoId =
                dbHelper.writableDatabase.insert(VideoContract.VideoEntry.TABLE_NAME, null, values)

            val video = getVideo(videoId)
            if (video != null) {
                _liveData.postValue(ModelState.VideoUploadingStart(video))
            }
        }
    }

    //will be called from service uploader job, we don't want to create lots of threads
    override fun updateEntryTransferredSize(id: Long, transferredSize: Long) {
        val values = ContentValues().apply {
            put(VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE, transferredSize)
        }
        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val updatedRows = dbHelper.writableDatabase
            .update(VideoContract.VideoEntry.TABLE_NAME, values, selection, arrayOf("$id"))

        val video = getVideo(id)
        if (updatedRows == 1 && video != null) {
            _liveData.postValue(ModelState.VideoUpdated(video))
        }
    }

    //will be called from service uploader job, we don't want to create lots of threads
    override fun updateEntryStatus(id: Long, newState: VideoUploadStatus) {
        val values = ContentValues().apply {
            put(VideoContract.VideoEntry.COLUMN_NAME_STATUS, newState.ordinal)
        }
        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val updatedRows = dbHelper.writableDatabase
            .update(VideoContract.VideoEntry.TABLE_NAME, values, selection, arrayOf("$id"))

        val video = getVideo(id)
        if (updatedRows == 1 && video != null) {
            if (newState == VideoUploadStatus.ERROR) {
                updateEntryTransferredSize(id, 0L) //required reupload
            } else {
                _liveData.postValue(ModelState.VideoUpdated(video))
            }
        }
    }

    //will be called from service uploader job, we don't want to create lots of threads
    override fun deleteEntry(id: Long) {
        val selection = "${VideoContract.VideoEntry.COLUMN_NAME_ID} = ?"
        val deletedRows = dbHelper.writableDatabase
            .delete(VideoContract.VideoEntry.TABLE_NAME, selection, arrayOf("$id"))

        if (deletedRows == 1) {
            _liveData.postValue(ModelState.DeleteVideo(id))
        }
    }

    override fun deleteAll() {
        thread {
            dbHelper.writableDatabase
                .rawQuery(VideoContract.VideoEntry.SQL_DELETE_ENTRIES, null)
                .close()
        }
    }

    override fun closeConnections() {
        dbHelper.close()
    }

    companion object {
        private fun Cursor.getVideo() =
            LocalVideo(
                id = getLong(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_ID)),
                uri = getString(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_URI)),
                title = getString(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_TITLE)),
                totalSize = getLong(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_TOTAL_SIZE)),
                transferredSize = getLong(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_TRANSFERRED_SIZE)),
                status = when (getInt(getColumnIndex(VideoContract.VideoEntry.COLUMN_NAME_STATUS))) {
                    VideoUploadStatus.PENDING.ordinal -> VideoUploadStatus.PENDING
                    VideoUploadStatus.ERROR.ordinal -> VideoUploadStatus.ERROR
                    VideoUploadStatus.LOADING.ordinal -> VideoUploadStatus.LOADING
                    VideoUploadStatus.FINISHED.ordinal -> VideoUploadStatus.FINISHED
                    else -> throw IllegalStateException(
                        "Unknown state ${
                            getInt(
                                getColumnIndex(
                                    VideoContract.VideoEntry.COLUMN_NAME_STATUS
                                )
                            )
                        }"
                    )
                }
            )

    }

}
