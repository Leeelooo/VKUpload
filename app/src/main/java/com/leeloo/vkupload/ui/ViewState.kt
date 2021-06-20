package com.leeloo.vkupload.ui

import com.leeloo.vkupload.vo.LocalVideo
import com.leeloo.vkupload.vo.OnDeviceVideo
import com.leeloo.vkupload.vo.RemoteUser

data class ViewState(
    val isUserLoggedIn: Boolean,
    val user: RemoteUser?,
    val isLoading: Boolean,
    val isError: Boolean,
    val data: List<LocalVideo>,
    val onDeviceVideo: OnDeviceVideo?
) {

    fun userLogedIn() = ViewState(
        isUserLoggedIn = true,
        user = null,
        isLoading = false,
        isError = false,
        data = emptyList(),
        onDeviceVideo = null
    )

    fun userLoaded(user: RemoteUser) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        onDeviceVideo = this.onDeviceVideo
    )

    fun userLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = null,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        onDeviceVideo = this.onDeviceVideo
    )

    fun initialVideoLoading() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = true,
        isError = false,
        data = emptyList(),
        onDeviceVideo = this.onDeviceVideo
    )

    fun initialVideoLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = true,
        data = emptyList(),
        onDeviceVideo = this.onDeviceVideo
    )

    fun videosLoaded(data: List<LocalVideo>) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = data,
        onDeviceVideo = this.onDeviceVideo
    )

    fun updateVideo(video: LocalVideo) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = this.data.toMutableList().apply {
            for (i in this.indices) {
                if (this[i].id == video.id) {
                    this.removeAt(i)
                    this.add(i, video)
                    break
                }
            }
        },
        onDeviceVideo = this.onDeviceVideo
    )

    fun addVideo(video: LocalVideo) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = this.data.toMutableList().apply {
            this.add(0, video)
        },
        onDeviceVideo = this.onDeviceVideo
    )

    fun deleteVideo(videoId: Long) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = this.data.toMutableList().apply {
            for (i in this.indices) {
                if (this[i].id == videoId) {
                    this.removeAt(i)
                    break
                }
            }
        },
        onDeviceVideo = this.onDeviceVideo
    )

    fun videoSelected(onDeviceVideo: OnDeviceVideo) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        onDeviceVideo = onDeviceVideo
    )

    fun dismissDialog() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        onDeviceVideo = null
    )

    companion object {
        fun init() = ViewState(
            isUserLoggedIn = false,
            user = null,
            isLoading = false,
            isError = false,
            data = emptyList(),
            onDeviceVideo = null
        )
    }

}