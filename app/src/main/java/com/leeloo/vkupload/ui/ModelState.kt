package com.leeloo.vkupload.ui

import com.leeloo.vkupload.vo.VKUser
import com.leeloo.vkupload.vo.Video

sealed class ModelState {
    abstract fun reduce(oldState: ViewState): ViewState

    object Init : ModelState() {
        override fun reduce(oldState: ViewState): ViewState =
            ViewState.init()
    }

    object UserLoggedIn : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.userLogedIn()
    }

    data class UserLoaded(
        private val user: VKUser
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
        private val videos: List<Video>
    ) : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.videosLoaded(videos)
    }

    data class VideosLoadingFailed(
        private val error: Exception
    ) : ModelState() {
        override fun reduce(oldState: ViewState) = oldState.initialVideoLoadingError()
    }

    data class VideoUpdated(
        private val video: Video
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState
    }

    data class VideoUploadingStart(
        private val video: Video
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState
    }

    data class DeleteVideo(
        private val videoId: Long
    ) : ModelState() {
        override fun reduce(oldState: ViewState): ViewState

    }

}