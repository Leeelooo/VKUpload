package com.leeloo.vkupload.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leeloo.vkupload.requests.GetUserRequest
import com.leeloo.vkupload.ui.ModelState
import com.leeloo.vkupload.vo.VKUser
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback

interface UserRepository {
    val liveData: LiveData<ModelState>
    fun userLoggedIn()
    fun clear()

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

    override fun clear() {
        _liveData.value = ModelState.Init
    }

    private fun requestUser(
        userIds: IntArray = intArrayOf(),
        fields: String = "photo_200"
    ) {
        VK.execute(
            GetUserRequest(userIds, fields), object : VKApiCallback<VKUser> {
                override fun fail(error: Exception) {
                    if (VK.isLoggedIn()) {
                        _liveData.postValue(ModelState.UserLoadingFailed(error))
                    }
                }

                override fun success(result: VKUser) {
                    _liveData.postValue(ModelState.UserLoaded(result))
                }
            })
    }
}