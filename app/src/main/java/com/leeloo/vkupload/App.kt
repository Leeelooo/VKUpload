package com.leeloo.vkupload

import android.app.Application
import com.leeloo.vkupload.data.local.VideoRepository
import com.leeloo.vkupload.data.local.VideoUploadRepository
import com.leeloo.vkupload.data.local.createPrefs
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiConfig
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        createPrefs(context = this)
        VideoRepository.createInstance(this)
        VideoUploadRepository.createInstance(this)

        VK.setConfig(
            VKApiConfig(
                appId = 7881815,
                version = "5.131",
                lang = Locale.getDefault().language,
                context = this,
                validationHandler = null
            )
        )
    }

}