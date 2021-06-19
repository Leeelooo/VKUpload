package com.leeloo.vkupload.ui

import com.leeloo.vkupload.vo.VKUser
import com.leeloo.vkupload.vo.Video

data class ViewState(
    val isUserLoggedIn: Boolean,
    val user: VKUser?,
    val isLoading: Boolean,
    val isError: Boolean,
    val data: List<Video>
) {

    fun userLogedIn() = ViewState(
        isUserLoggedIn = true,
        user = null,
        isLoading = false,
        isError = false,
        data = emptyList()
    )

    fun userLoaded(user: VKUser) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = user,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data
    )

    fun userLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = null,
        isLoading = this.isLoading,
        isError = this.isError,
        data = this.data
    )

    fun initialVideoLoading() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = true,
        isError = false,
        data = emptyList()
    )

    fun initialVideoLoadingError() = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = true,
        data = emptyList()
    )

    fun videosLoaded(data: List<Video>) = ViewState(
        isUserLoggedIn = this.isUserLoggedIn,
        user = this.user,
        isLoading = false,
        isError = false,
        data = data
    )

    companion object {
        fun init() = ViewState(
            isUserLoggedIn = false,
            user = null,
            isLoading = false,
            isError = false,
            data = emptyList()
        )
    }

}