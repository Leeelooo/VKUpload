package com.leeloo.vkupload.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leeloo.vkupload.R
import com.leeloo.vkupload.data.local.changeToStopMode
import com.leeloo.vkupload.data.local.changeToUnstoppableMode
import com.leeloo.vkupload.data.local.isInStopMode
import com.leeloo.vkupload.service.UploadingService
import com.leeloo.vkupload.utils.CircularOutlineProvider
import com.leeloo.vkupload.utils.getVideo
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_content.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var popup: PopupMenu
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            viewModel.onLogout()
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null) {
            viewModel.onVideoSelected(getVideo(this, it))
        }
    }

    override fun onStart() {
        super.onStart()
        UploadingService.stop = false
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
        if (isInStopMode()) {
            UploadingService.stop = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (VK.isLoggedIn()) {
                viewModel.onLogin()
            } else {
                viewModel.onLogout()
            }
        }
        VK.addTokenExpiredHandler(tokenTracker)

        initViews()
        viewModel.viewState.observe(this, this::render)
        viewModel.videoCreationLiveData.observe(this) {
            if (it != null) {
                UploadingService.enqueueWork(this, it)
            }
        }
    }

    private fun initViews() {
        main_profile.setOnClickListener {
            if (!VK.isLoggedIn()) {
                VK.login(this, arrayListOf(VKScope.VIDEO))
            }
        }
        main_profile.outlineProvider = CircularOutlineProvider
        main_profile.clipToOutline = true

        popup = PopupMenu(this, menu)
        popup.setOnMenuItemClickListener { onOptionsItemSelected(it) }
        popup.menuInflater.inflate(R.menu.menu, popup.menu)
        menu.setOnClickListener {
            popup.menu.findItem(R.id.item_menu_mode).isChecked = isInStopMode()
            popup.show()
        }

        videoAdapter = VideoAdapter(
            loginListener = { VK.login(this, arrayListOf(VKScope.VIDEO)) },
            reloadListener = { viewModel.onReload() }
        )
        video_recycler.adapter = videoAdapter
        video_recycler.layoutManager = LinearLayoutManager(this)

        video_upload_fab.setOnClickListener {
            getContent.launch(
                arrayOf(
                    "video/x-msvideo", //avi
                    "video/mp4", //mp4
                    "video/3gpp", //3gp
                    "video/x-flv", //flv
                    "video/quicktime", //mov
                    "video/x-ms-wmv" //wmv
                )
            )
        }

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_content)
        bottomSheetDialog.dismissWithAnimation = true

        bottomSheetDialog.btn_share.setOnClickListener {
            viewModel.onSendClicked(bottomSheetDialog.title_edit.text.toString())
        }

        bottomSheetDialog.bottom_sheet_exit.setOnClickListener {
            viewModel.onDismiss()
        }
        bottomSheetDialog.setOnDismissListener {
            viewModel.onDismiss()
        }

        bottomSheetDialog.setOnShowListener {
            bottomSheetDialog.bottom_sheet.post {
                val d = it as BottomSheetDialog
                d.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun render(viewState: ViewState) {
        if (viewState.onDeviceVideo != null) {
            Glide.with(bottomSheetDialog.choosen_video_preview)
                .asBitmap()
                .load(viewState.onDeviceVideo.uri)
                .override(500)
                .into(bottomSheetDialog.choosen_video_preview)
            if (!bottomSheetDialog.isShowing) {
                bottomSheetDialog.show()
                bottomSheetDialog.title_edit.setText(viewState.onDeviceVideo.title)
            }
        } else {
            Glide.with(bottomSheetDialog.choosen_video_preview)
                .clear(bottomSheetDialog.choosen_video_preview)
            bottomSheetDialog.title_edit.setText("")
            bottomSheetDialog.title_edit.clearFocus()
            bottomSheetDialog.dismiss()
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_menu_mode -> {
                if (item.isChecked) {
                    changeToStopMode()
                } else {
                    changeToUnstoppableMode()
                }
                true
            }
            R.id.item_menu_exit -> {
                viewModel.onLogout()
                true
            }
            else -> throw IllegalStateException("wrong menu id ${item.itemId}")
        }
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
        }
    }

}