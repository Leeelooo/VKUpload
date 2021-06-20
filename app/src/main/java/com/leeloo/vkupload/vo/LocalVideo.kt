package com.leeloo.vkupload.vo

data class LocalVideo(
    val id: Long,
    val uri: String,
    val title: String,
    val totalSize: Long,
    val transferredSize: Long,
    val status: VideoUploadStatus
)

enum class VideoUploadStatus {
    PENDING,
    LOADING,
    ERROR,
    FINISHED
}