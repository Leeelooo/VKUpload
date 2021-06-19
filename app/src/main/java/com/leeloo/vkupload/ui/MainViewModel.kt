package com.leeloo.vkupload.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    fun onLogin() {

    }

    fun onLogout() {

    }

    fun onReload() {

    }

    fun onDismiss() {

    }

    fun onSendClicked() {

    }

    fun onVideoSelected(videoUri: Uri?) {

    }

    fun onVideoNameChanged(videoName: String) {

    }

}