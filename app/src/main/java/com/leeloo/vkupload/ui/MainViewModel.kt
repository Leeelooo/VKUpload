package com.leeloo.vkupload.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.leeloo.vkupload.data.local.VideoRepository
import com.leeloo.vkupload.data.remote.UserRepository
import com.leeloo.vkupload.vo.OnDeviceVideo
import com.vk.api.sdk.VK

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
            liveData.addSource(_modelState) {
                if (it is ModelState.VideoUploadingStart) {
                    _videoCreationLiveData.value = it.video.id
                }
                liveData.value = it.reduce(liveData.value!!)
            }
        }
    }
    val viewState: LiveData<ViewState>
        get() = _viewState

    private val _videoCreationLiveData = MutableLiveData<Long>()
    val videoCreationLiveData: LiveData<Long>
        get() = _videoCreationLiveData

    fun onLogin() {
        userRepository.userLoggedIn()
        videoRepository.getVideos()
    }

    fun onLogout() {
        VK.logout()
        videoRepository.deleteAll()
        _modelState.value = ModelState.Init
    }

    fun onReload() {
        videoRepository.getVideos()
    }

    fun onDismiss() {
        _modelState.value = ModelState.DismissDialog
    }

    fun onSendClicked(title: String) {
        videoRepository.createNewEntry(
            onDeviceVideo = _viewState.value!!.onDeviceVideo!!,
            title = title
        )
        _modelState.value = ModelState.DismissDialog
    }

    fun onVideoSelected(onDeviceVideo: OnDeviceVideo) {
        _modelState.value = ModelState.VideoSelected(onDeviceVideo)
    }

    fun onStop() {
        _modelState.value = ModelState.NothingState
        _videoCreationLiveData.value = null
    }

}

