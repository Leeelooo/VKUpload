package com.leeloo.vkupload

import android.app.Application
import com.leeloo.vkupload.data.local.VideoRepository
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiConfig
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //since sdk uses v5.90(WHERE IS FAVE) and en by default...
        //why are you guys using old api version and not locale lol
        //or this is secret for hours of debugging
        VideoRepository.createInstance(this)
        VK.setConfig(
            VKApiConfig(
                version = "5.131",
                lang = Locale.getDefault().language,
                context = this,
                validationHandler = null
            )
        )
    }

}