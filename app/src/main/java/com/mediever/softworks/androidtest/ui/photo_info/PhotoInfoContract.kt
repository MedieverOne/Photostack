package com.mediever.softworks.androidtest.ui.photo_info

import com.mediever.softworks.androidtest.models.Picture

interface PhotoInfoContract {
    interface PhotoInfoView {
        fun onSuccess(picture: Picture?)
        fun onError(throwable:Throwable)
    }
    
    interface PhotoInfoPresenter {
        fun getPicture(id: Int)
        fun onStop()
    }
}