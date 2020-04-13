package com.mediever.softworks.androidtest.repository

//Bye CA
import com.mediever.softworks.androidtest.models.Picture
import io.reactivex.Observable


interface RepositoryContract {
    interface PicturesRepositoryContract {
        fun downloadNewPage(popular: Boolean)
        fun getAllByType(popular: Boolean): Observable<List<Picture>>
        fun updatePicturesByType(popular: Boolean)
        fun initPicturesByType(popular: Boolean)
        fun onStop()
    }

    interface PicturesPresenterContract {
        fun onSuccess()
        fun onFailure()
        fun emptyRepository()
        fun endOfData()
        fun dataHasChanged(picturesList: List<Picture>)
    }
}