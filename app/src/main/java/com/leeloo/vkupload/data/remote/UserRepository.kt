package com.leeloo.vkupload.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leeloo.vkupload.requests.GetUserRequest
import com.leeloo.vkupload.ui.ModelState
import com.leeloo.vkupload.vo.RemoteUser
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback

interface UserRepository {
    val liveData: LiveData<ModelState>
    fun userLoggedIn()

    companion object {
        private lateinit var _INSTANCE: UserRepository
        fun getInstance(): UserRepository {
            if (!Companion::_INSTANCE.isInitialized) {
                _INSTANCE = UserRepositoryImpl()
            }
            return _INSTANCE
        }
    }
}

class UserRepositoryImpl : UserRepository {
    private val _liveData = MutableLiveData<ModelState>()
    override val liveData: LiveData<ModelState>
        get() = _liveData

    override fun userLoggedIn() {
        _liveData.value = ModelState.UserLoggedIn
        requestUser()
    }

    private fun requestUser(
        userIds: IntArray = intArrayOf(),
        fields: String = "photo_200"
    ) {
        VK.execute(
            GetUserRequest(userIds, fields), object : VKApiCallback<RemoteUser> {
                override fun fail(error: Exception) {
                    if (VK.isLoggedIn()) {
                        _liveData.postValue(ModelState.UserLoadingFailed(error))
                    } else {
                        _liveData.postValue(ModelState.Init)
                    }
                }

                override fun success(result: RemoteUser) {
                    _liveData.postValue(ModelState.UserLoaded(result))
                }
            })
    }
}