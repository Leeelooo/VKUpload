package com.leeloo.vkupload.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.leeloo.vkupload.R
import com.leeloo.vkupload.utils.VideoDiffCallback
import com.leeloo.vkupload.vo.Video

enum class RecyclerState(val viewType: Int) {
    VIDEO(0),
    NOT_LOGGED(300000),
    LOADING(400000),
    EMPTY(500000),
    ERROR(600000)
}

class VideoAdapter(
    private val loginListener: () -> Unit,
    private val reloadListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items: MutableList<Video> = mutableListOf()
    private var state: RecyclerState = RecyclerState.NOT_LOGGED

    private val layoutId: Int
        get() =
            when (state) {
                RecyclerState.NOT_LOGGED -> R.layout.item_login
                RecyclerState.LOADING -> R.layout.item_loading
                RecyclerState.VIDEO -> R.layout.item_video
                RecyclerState.EMPTY -> R.layout.item_empty
                else -> R.layout.item_error
            }

    private val buttonId: Int
        get() =
            when (state) {
                RecyclerState.NOT_LOGGED -> R.id.login_button
                else -> R.id.retry_button
            }

    fun updateData(items: List<Video>, state: RecyclerState) {
        if (state == RecyclerState.VIDEO) {
            if (this.state == RecyclerState.VIDEO) {
                this.state = state
            } else {
                this.state = state
                notifyItemRemoved(0)
            }
            val diffResult = DiffUtil.calculateDiff(
                VideoDiffCallback(
                    this.items,
                    items
                )
            )
            this.items.clear()
            this.items.addAll(items)
            diffResult.dispatchUpdatesTo(this)
        } else {
            if (this.state == RecyclerState.VIDEO) {
                notifyItemRangeRemoved(0, itemCount)
                this.items.clear()
                this.state = state
                notifyItemInserted(0)
            } else {
                this.state = state
                notifyItemChanged(0)
            }
        }
    }

    override fun getItemCount() = if (items.isNotEmpty()) items.size else 1

    override fun getItemViewType(position: Int) =
        if (position == 0) state.viewType else position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (state) {
            RecyclerState.VIDEO ->
                VideoViewHolder(
                    view = LayoutInflater.from(parent.context).inflate(
                        layoutId,
                        parent,
                        false
                    )
                )
            RecyclerState.LOADING, RecyclerState.EMPTY ->
                LoadingViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        layoutId,
                        parent,
                        false
                    )
                )
            else ->
                MessageViewHolder(
                    view = LayoutInflater.from(parent.context).inflate(
                        layoutId,
                        parent,
                        false
                    ),
                    buttonId = buttonId,
                    listener = if (state == RecyclerState.NOT_LOGGED) loginListener else reloadListener
                )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is VideoViewHolder) {
            holder.clear()
        }
    }

}