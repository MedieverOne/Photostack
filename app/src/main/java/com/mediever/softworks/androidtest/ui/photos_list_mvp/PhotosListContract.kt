package com.mediever.softworks.androidtest.ui.photos_list_mvp

import com.mediever.softworks.androidtest.models.Picture

interface PhotosListContract {
    interface PhotosListView {
        fun dataHasChanged(picturesList: List<Picture>)
        fun showProgress()
        fun hideProgress()
        fun onSuccess(picturesList: List<Picture>)
        fun onError()
        fun endOfData()
    }

    interface PhotosListPresenter {
        fun updatePicturesByType()
        fun initPicturesByType()
        fun getPicturesPage()
        fun getAllByActualType()
        fun onStop()
    }
}