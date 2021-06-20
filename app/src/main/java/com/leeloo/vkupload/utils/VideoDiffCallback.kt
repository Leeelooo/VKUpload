package com.leeloo.vkupload.utils

import androidx.recyclerview.widget.DiffUtil
import com.leeloo.vkupload.vo.LocalVideo

class VideoDiffCallback(
    private val oldList: List<LocalVideo>,
    private val newList: List<LocalVideo>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}