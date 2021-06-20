package com.leeloo.vkupload.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.leeloo.vkupload.data.local.VideoRepository
import com.leeloo.vkupload.data.remote.UserRepository
import com.leeloo.vkupload.vo.LocalVideo
import java.util.*

class MainViewModel : ViewModel() {
    private val videoRepository = VideoRepository.getInstance()
    private val userRepository = UserRepository.getInstance()

    private val _modelState: MediatorLiveData<ModelState> by lazy {
        MediatorLiveData<ModelState>().also { liveData ->
            liveData.addSource(videoRepository.liveData) { liveData.value = it }
            liveData.addSource(userRepository.liveData) { liveData.value = it }
        }
    }

    private val _viewState: MediatorLiveData<ViewState> by lazy {
        MediatorLiveData<ViewState>().also { liveData ->
            liveData.value = ViewState.init()
            liveData.addSource(_modelState) { liveData.value = it.reduce(liveData.value!!) }
        }
    }
    val viewState: LiveData<ViewState>
        get() = _viewState


    fun onLogin() {
        userRepository.userLoggedIn()
        videoRepository.getVideos()
    }

    fun onLogout() {
        userRepository.clear()
        videoRepository.deleteAll()
    }

    fun onReload() {
        videoRepository.getVideos()
    }

    fun onDismiss() {
        _modelState.value = ModelState.DismissDialog
    }

    fun onSendClicked(title: String) {
        videoRepository.createNewEntry(
            uri = _viewState.value!!.localVideo!!.uri,
            title = title,
            sessionUUID = UUID.randomUUID()
        )
        _modelState.value = ModelState.DismissDialog
    }

    fun onVideoSelected(localVideo: LocalVideo) {
        _modelState.value = ModelState.VideoSelected(localVideo)
    }

    fun onPermissionGranted() {

    }

    fun onPermissionNotGranted() {

    }

    fun onPermissionExists() {

    }

}

