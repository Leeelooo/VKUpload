package com.leeloo.vkupload.ui

import android.net.Uri
import com.leeloo.vkupload.vo.VKUser
import com.leeloo.vkupload.vo.Video

data class ViewState(
    val isUserLoggedIn: Boolean,
    val user: VKUser?,
    val isLoading: Boolean,
    val isError: Boolean,
    val data: List<Video>,
    val isDialogOpened: Boolean,
    val videoUri: Uri?,
    val videoTitle: String
) {

    fun userLogedIn() = ViewState(
        isUserLoggedIn = true,
        user = null,
        isLoading = false,
        isError = false,
        data = emptyList(),
        isDialogOpened = false,
        videoUri = null,
        videoTitle = ""
    )

    fun userLoaded(user: VKUser) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun userLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = null,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun initialVideoLoading() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = true,
        isError = false,
        data = emptyList(),
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun initialVideoLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = true,
        data = emptyList(),
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun videosLoaded(data: List<Video>) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = data,
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun updateVideo(video: Video) = ViewState(
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
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun addVideo(video: Video) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = this.data.toMutableList().apply {
            this.add(0, video)
        },
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
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
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun videoSelected(videoUri: Uri?) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    fun titleChanged(title: String) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data,
        isDialogOpened = this.isDialogOpened,
        videoUri = this.videoUri,
        videoTitle = this.videoTitle
    )

    companion object {
        fun init() = ViewState(
            isUserLoggedIn = false,
            user = null,
            isLoading = false,
            isError = false,
            data = emptyList(),
            isDialogOpened = false,
            videoUri = null,
            videoTitle = ""
        )
    }

}