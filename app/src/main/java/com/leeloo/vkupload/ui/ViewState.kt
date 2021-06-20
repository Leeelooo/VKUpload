package com.leeloo.vkupload.ui

import com.leeloo.vkupload.vo.LocalVideo
import com.leeloo.vkupload.vo.VKUser
import com.leeloo.vkupload.vo.VKVideoUpload

data class ViewState(
    val isUserLoggedIn: Boolean,
    val user: VKUser?,
    val isLoading: Boolean,
    val isError: Boolean,
    val data: List<VKVideoUpload>,
    val localVideo: LocalVideo?
) {

    fun userLogedIn() = ViewState(
        isUserLoggedIn = true,
        user = null,
        isLoading = false,
        isError = false,
        data = emptyList(),
        localVideo = null
    )

    fun userLoaded(user: VKUser) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        localVideo = this.localVideo
    )

    fun userLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = null,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        localVideo = this.localVideo
    )

    fun initialVideoLoading() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = true,
        isError = false,
        data = emptyList(),
        localVideo = this.localVideo
    )

    fun initialVideoLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = true,
        data = emptyList(),
        localVideo = this.localVideo
    )

    fun videosLoaded(data: List<VKVideoUpload>) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = data,
        localVideo = this.localVideo
    )

    fun updateVideo(video: VKVideoUpload) = ViewState(
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
        localVideo = this.localVideo
    )

    fun addVideo(video: VKVideoUpload) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = this.data.toMutableList().apply {
            this.add(0, video)
        },
        localVideo = this.localVideo
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
        localVideo = this.localVideo
    )

    fun videoSelected(localVideo: LocalVideo) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        localVideo = localVideo
    )

    fun dismissDialog() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        localVideo = null
    )

    companion object {
        fun init() = ViewState(
            isUserLoggedIn = false,
            user = null,
            isLoading = false,
            isError = false,
            data = emptyList(),
            localVideo = null
        )
    }

}