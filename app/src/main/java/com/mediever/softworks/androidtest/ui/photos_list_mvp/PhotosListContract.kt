package com.mediever.softworks.androidtest.ui.photos_list_mvp

import com.mediever.softworks.androidtest.models.Picture

interface PhotosListContract {
    interface PhotosListView {
        fun showProgress()
        fun hideProgress()
        fun onSuccess (picturesList: List<Picture>)
        fun onError ()
        fun endOfData ()
    }

    interface PhotosListPresenter {
        fun updateData()
        fun initData()
        fun getPicturesPage()
        fun getAll()
        fun onStop()
    }
}