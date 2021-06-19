package com.leeloo.vkupload.utils

import androidx.recyclerview.widget.DiffUtil
import com.leeloo.vkupload.vo.Video

class VideoDiffCallback(
    private val oldList: List<Video>,
    private val newList: List<Video>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}