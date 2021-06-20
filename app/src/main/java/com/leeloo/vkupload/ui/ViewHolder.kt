package com.leeloo.vkupload.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.leeloo.vkupload.R
import com.leeloo.vkupload.utils.SmoothOutlineProvider
import com.leeloo.vkupload.utils.formatFileSize
import com.leeloo.vkupload.vo.VKVideoUpload
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

    private lateinit var data: VKVideoUpload

    fun bind(item: VKVideoUpload) {
        data = item

        Glide.with(preview)
            .load(item.uri)
            .into(preview)
        title.text = item.title
        progress.text = progress.resources.getString(
            R.string.text_video_uploading_progress,
            item.transferredSize.formatFileSize(progress.context),
            item.totalSize.formatFileSize(progress.context)
        )
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