package com.mediever.softworks.androidtest.repository
//Bye CA
import com.mediever.softworks.androidtest.models.Picture
import io.reactivex.Observable


interface RepositoryContract {
    interface PicturesRepositoryContract {
        fun downloadNewPage(page:Int,new:Boolean,popular:Boolean)
        fun getAll(new:Boolean,popular:Boolean) : Observable<List<Picture>>
        fun updateData(popular:Boolean)
        fun onStop()
    }

    interface PicturesPresenterContract {
        fun onSuccess()
        fun onFailure()
        fun emptyRepository()
        fun endOfData()
    }
}