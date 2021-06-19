package com.leeloo.vkupload.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.leeloo.vkupload.R

object SmoothOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(
            0,
            0,
            view.width,
            view.height,
            view.resources.getDimension(R.dimen.small_corner_radius)
        )
    }
}