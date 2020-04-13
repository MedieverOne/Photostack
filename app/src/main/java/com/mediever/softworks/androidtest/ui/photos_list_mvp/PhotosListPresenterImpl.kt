package com.mediever.softworks.androidtest.ui.photos_list_mvp

import android.util.Log
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.models.PicturesList
import com.mediever.softworks.androidtest.repository.PicturesRepository
import com.mediever.softworks.androidtest.repository.RepositoryContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PhotosListPresenterImpl(
    var view: PhotosListContract.PhotosListView?,
    var popular: Boolean
) : PhotosListContract.PhotosListPresenter,
    RepositoryContract.PicturesPresenterContract {
    private var disposable: Disposable = CompositeDisposable()
    private val repository: RepositoryContract.PicturesRepositoryContract = PicturesRepository(this)


    // Общение с view
    override fun updatePicturesByType() = repository.updatePicturesByType(popular)

    override fun getPicturesPage() {
        view!!.showProgress()
        Log.d("HALO", "presenter get Pic PAGE")
        repository.downloadNewPage(popular)
    }

    override fun getAllByActualType() {
        disposable = repository.getAllByType(popular)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                view!!.onSuccess(response.filter { (it.popular == popular) })
            }
    }

    override fun onStop() {
        disposable.dispose()
        repository.onStop()
    }

    override fun initPicturesByType() {
        repository.initPicturesByType(popular)
    }

    override fun dataHasChanged(picturesList: List<Picture>) {
        view!!.dataHasChanged(picturesList.filter { (it.popular == popular) })
    }

    // Общение с репозиторием
    override fun onSuccess() {
        view!!.hideProgress()
        getAllByActualType()
    }

    override fun onFailure() {
        view!!.hideProgress()
    }

    override fun emptyRepository() {
        view!!.hideProgress()
        view!!.onError()
    }

    override fun endOfData() {
        view!!.hideProgress()
        view!!.endOfData()
    }

}