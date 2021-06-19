package com.leeloo.vkupload.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.leeloo.vkupload.data.local.VideoRepository
import com.leeloo.vkupload.data.remote.UserRepository
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

    }

    fun onSendClicked() {
        videoRepository.createNewEntry(
            uri = _viewState.value!!.videoUri!!,
            title = _viewState.value!!.videoTitle,
            sessionUUID = UUID.randomUUID()
        )
    }

    fun onVideoSelected(videoUri: Uri?) {
        _modelState.value = ModelState.VideoSelected(videoUri)
    }

    fun onVideoNameChanged(videoName: String) {
        _modelState.value = ModelState.VideoTitleChanged(videoName)
    }

    override fun onCleared() {
        super.onCleared()
        videoRepository.closeConnections()
    }
}

