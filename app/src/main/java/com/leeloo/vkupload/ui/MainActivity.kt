package com.leeloo.vkupload.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.leeloo.vkupload.R
import com.leeloo.vkupload.utils.CircularOutlineProvider
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var videoAdapter: VideoAdapter

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            viewModel.onLogout()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null && viewModel.viewState.value == null) {
            if (VK.isLoggedIn()) {
                viewModel.onLogin()
            } else {
                viewModel.onLogout()
            }
        }
        VK.addTokenExpiredHandler(tokenTracker)

        initViews()
        viewModel.viewState.observe(this, this::render)
    }

    private fun initViews() {
        main_profile.setOnClickListener {
            if (VK.isLoggedIn()) {
                VK.clearAccessToken(this)
                viewModel.onLogout()
            } else {
                VK.login(this, arrayListOf(VKScope.VIDEO))
            }
        }
        main_profile.outlineProvider = CircularOutlineProvider
        main_profile.clipToOutline = true

        videoAdapter = VideoAdapter(
            loginListener = { VK.login(this, arrayListOf(VKScope.VIDEO)) },
            reloadListener = { viewModel.onReload() }
        )
        video_recycler.adapter = videoAdapter
        video_recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun render(viewState: ViewState) {
        Glide.with(main_profile)
            .load(viewState.user?.photoURL)
            .fallback(R.drawable.ic_profile)
            .into(main_profile)

        val state = when {
            !viewState.isUserLoggedIn -> RecyclerState.NOT_LOGGED
            viewState.isLoading -> RecyclerState.LOADING
            viewState.isError -> RecyclerState.ERROR
            viewState.data.isEmpty() -> RecyclerState.EMPTY
            else -> RecyclerState.VIDEO
        }

        video_upload_fab.isVisible = viewState.isUserLoggedIn
        videoAdapter.updateData(viewState.data, state)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                viewModel.onLogin()
            }

            override fun onLoginFailed(errorCode: Int) {

            }
        }
        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK)
                    viewModel.onVideoSelected(data?.data)
            }
        }
    }


}