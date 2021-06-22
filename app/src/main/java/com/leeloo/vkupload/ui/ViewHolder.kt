package com.leeloo.vkupload.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.leeloo.vkupload.R
import com.leeloo.vkupload.utils.SmoothOutlineProvider
import com.leeloo.vkupload.utils.formatFileSize
import com.leeloo.vkupload.vo.LocalVideo
import com.leeloo.vkupload.vo.VideoUploadStatus
import kotlinx.android.synthetic.main.item_video.view.*

class VideoViewHolder(
    view: View,
) : RecyclerView.ViewHolder(view) {
    private val preview = view.video_preview.apply {
        outlineProvider = SmoothOutlineProvider
        clipToOutline = true
    }
    private val title = view.video_title
    private val progress = view.video_upload_progress
    private val statusIcon = view.video_upload_status

    private lateinit var data: LocalVideo

    fun bind(item: LocalVideo) {
        data = item
        Glide.with(preview)
            .load(item.uri)
            .into(preview)
        title.text = item.title

        val statusIconResource = when (item.status) {
            VideoUploadStatus.PENDING -> R.drawable.ic_outline_pending_24
            VideoUploadStatus.LOADING -> R.drawable.ic_outline_save_alt_24
            VideoUploadStatus.ERROR -> R.drawable.ic_outline_error_outline_24
            VideoUploadStatus.FINISHED -> R.drawable.ic_outline_done_24
        }
        Glide.with(statusIcon)
            .load(statusIconResource)
            .into(statusIcon)

        progress.text =
            when {
                data.totalSize == -1L ->
                    item.transferredSize.formatFileSize(progress.context)
                data.status == VideoUploadStatus.FINISHED ->
                    item.totalSize.formatFileSize(progress.context)
                else -> progress.resources.getString(
                    R.string.text_video_uploading_progress,
                    item.transferredSize.formatFileSize(progress.context),
                    item.totalSize.formatFileSize(progress.context)
                )
            }
    }

    fun clear() {
        Glide.with(preview).clear(preview)
    }

}

class MessageViewHolder(
    view: View,
    buttonId: Int,
    listener: () -> Unit
) : RecyclerView.ViewHolder(view) {
    init {
        view.findViewById<MaterialButton>(buttonId).setOnClickListener { listener() }
    }
}

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)