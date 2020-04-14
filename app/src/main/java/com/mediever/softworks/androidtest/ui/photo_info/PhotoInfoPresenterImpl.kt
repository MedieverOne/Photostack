package com.mediever.softworks.androidtest.ui.photo_info

import com.mediever.softworks.androidtest.models.Picture
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.realm.Realm

class PhotoInfoPresenterImpl(var view: PhotoInfoContract.PhotoInfoView?) :
    PhotoInfoContract.PhotoInfoPresenter {

    private var disposable: Disposable = CompositeDisposable()

    override fun getPicture(id: Int) {
        val realm: Realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val picture = realm.where(Picture::class.java).equalTo("id", id).findFirst()
        realm.cancelTransaction()
        view!!.onSuccess(picture)
    }

    override fun onStop() {
        disposable.dispose()
    }
}