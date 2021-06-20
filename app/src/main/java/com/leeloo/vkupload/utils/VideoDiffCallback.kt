package com.leeloo.vkupload.utils

import androidx.recyclerview.widget.DiffUtil
import com.leeloo.vkupload.vo.VKVideoUpload

class VideoDiffCallback(
    private val oldList: List<VKVideoUpload>,
    private val newList: List<VKVideoUpload>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}