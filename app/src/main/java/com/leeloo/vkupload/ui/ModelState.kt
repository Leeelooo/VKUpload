package com.leeloo.vkupload.ui

import com.leeloo.vkupload.vo.LocalVideo
import com.leeloo.vkupload.vo.OnDeviceVideo
import com.leeloo.vkupload.vo.RemoteUser

sealed class ModelState {
    abstract fun reduce(oldState: ViewState): ViewState

    object Init : ModelState() {
        override fun reduce(oldState: ViewState): ViewState =
            ViewState.init()
    }

    object UserLoggedIn : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.userLoggedIn()
    }

    data class UserLoaded(
        private val user: RemoteUser
    ) : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.userLoaded(user)
    }

    data class UserLoadingFailed(
        private val error: Exception
    ) : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.userLoadingError()
    }

    object VideosLoading : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.initialVideoLoading()
    }

    data class VideosLoaded(
        private val videos: List<LocalVideo>
    ) : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.videosLoaded(videos)
    }

    data class VideosLoadingFailed(
        private val error: Exception
    ) : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.initialVideoLoadingError()
    }

    data class VideoUpdated(
        private val video: LocalVideo
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState = oldState.updateVideo(video)
    }

    data class VideoUploadingStart(
        val video: LocalVideo
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState = oldState.addVideo(video)
    }

    data class DeleteVideo(
        private val videoId: Long
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState = oldState.deleteVideo(videoId)
    }

    data class VideoSelected(
        private val onDeviceVideo: OnDeviceVideo
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState =
            oldState.videoSelected(onDeviceVideo)
    }

    object DismissDialog : ModelState() {
        override fun reduce(oldState: ViewState): ViewState = oldState.dismissDialog()
    }

    object NothingState : ModelState() {
        override fun reduce(oldState: ViewState): ViewState = oldState
    }

}