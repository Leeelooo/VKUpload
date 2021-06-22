package com.leeloo.vkupload.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.JobIntentService
import androidx.core.os.bundleOf
import com.leeloo.vkupload.data.local.VideoRepository
import com.leeloo.vkupload.data.local.VideoUploadRepository
import com.leeloo.vkupload.requests.PostVideoRequest
import com.leeloo.vkupload.utils.getMimeType
import com.leeloo.vkupload.vo.LocalVideo
import com.leeloo.vkupload.vo.LocalVideoUpload
import com.leeloo.vkupload.vo.RemoteVideoPost
import com.leeloo.vkupload.vo.VideoUploadStatus
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.source
import java.io.IOException

class UploadingService : JobIntentService() {
    private val videoRepository = VideoRepository.getInstance()
    private val videoUploadRepository = VideoUploadRepository.getInstance()

    override fun onHandleWork(intent: Intent) {
        val localVideoId = intent.extras?.getLong(FIELD_LOCAL_VIDEO_ID) ?: return
        val localVideo = videoRepository.getVideo(localVideoId) ?: return
        VK.execute(
            PostVideoRequest(name = localVideo.title),
            object : VKApiCallback<RemoteVideoPost> {
                override fun fail(error: Exception) {
                    videoRepository.updateEntryStatus(localVideoId, VideoUploadStatus.ERROR)
                }

                override fun success(result: RemoteVideoPost) {
                    val uploadTask =
                        videoUploadRepository.createTask(result, localVideoId)

                    if (uploadTask == null) {
                        videoRepository.updateEntryStatus(localVideoId, VideoUploadStatus.ERROR)
                    } else {
                        videoRepository.updateEntryStatus(localVideoId, VideoUploadStatus.LOADING)
                        uploadFile(localVideo, uploadTask)
                    }
                }
            }
        )
    }

    private fun uploadFile(localVideo: LocalVideo, uploadTask: LocalVideoUpload) {
        val fileUri = Uri.parse(localVideo.uri)

        val reqBody = object : RequestBody() {
            override fun contentLength(): Long = localVideo.totalSize

            override fun contentType(): MediaType? =
                getMimeType(this@UploadingService, fileUri)?.toMediaTypeOrNull()

            override fun writeTo(sink: BufferedSink) {
                this@UploadingService.contentResolver.openInputStream(fileUri)?.source()?.use {
                    var currentProgress = localVideo.transferredSize
                    var lastRead = 0L
                    var untilEntityUpdate = UPDATE_SIZE_EVERY

                    while (lastRead != -1L) {
                        if (!stop) { //LLC PEPEGA SOLUTIONS
                            lastRead = it.read(sink.buffer, SEGMENT_SIZE)
                            currentProgress += lastRead
                            untilEntityUpdate -= lastRead
                            sink.flush()
                            if (untilEntityUpdate < 0L) {
                                untilEntityUpdate = UPDATE_SIZE_EVERY
                                videoRepository.updateEntryTransferredSize(
                                    localVideo.id,
                                    currentProgress
                                )
                            }
                        }
                    }
                }
            }

        }

        val requestBody: RequestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "video_file",
                    localVideo.title,
                    reqBody
                )
                .build()

        val request: Request = Request.Builder()
            .url(uploadTask.uploadUrl)
            .addHeader("Session-ID", uploadTask.sessionUUID)
            .addHeader("Connection", "keep-alive")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder().build()
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    videoRepository.updateEntryStatus(localVideo.id, VideoUploadStatus.ERROR)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.body?.string()?.contains("video_id") == true) {
                        videoRepository.updateEntryStatus(localVideo.id, VideoUploadStatus.FINISHED)
                        videoRepository.updateEntryTransferredSize(
                            localVideo.id,
                            localVideo.totalSize
                        )
                        videoUploadRepository.deleteUploadTask(uploadTask.id)
                    } else {
                        videoRepository.updateEntryStatus(localVideo.id, VideoUploadStatus.ERROR)
                    }
                }
            })
        } catch (ex: Exception) {
            videoRepository.updateEntryStatus(localVideo.id, VideoUploadStatus.ERROR)
        }
    }

    companion object {
        private const val FIELD_LOCAL_VIDEO_ID = "LOCAL_VIDEO_ID"
        private const val UPDATE_SIZE_EVERY = 200 * 1000L //200kB
        private const val SEGMENT_SIZE = 2048L
        const val jobId = 43421
        var stop = false

        fun enqueueWork(context: Context, localVideoId: Long) {
            enqueueWork(
                context,
                UploadingService::class.java,
                jobId,
                getUploadingWorkIntent(context, localVideoId)
            )
        }

        fun getUploadingWorkIntent(context: Context, localVideoId: Long) =
            Intent(context, UploadingService::class.java).apply {
                this.putExtras(bundleOf(FIELD_LOCAL_VIDEO_ID to localVideoId))
            }

    }

}