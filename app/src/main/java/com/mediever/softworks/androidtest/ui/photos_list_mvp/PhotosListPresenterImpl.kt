package com.mediever.softworks.androidtest.ui.photos_list_mvp

import com.mediever.softworks.androidtest.repository.PicturesRepository
import com.mediever.softworks.androidtest.repository.RepositoryContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PhotosListPresenterImpl(var view: PhotosListContract.PhotosListView?,
                              var new: Boolean,
                              var popular: Boolean) : PhotosListContract.PhotosListPresenter,
                                            RepositoryContract.PicturesPresenterContract {
    var disposable: Disposable? = null
    private val repository: RepositoryContract.PicturesRepositoryContract = PicturesRepository(this)

    // Общение с view
    override fun updateData() = repository.updateData(popular)

    override fun getPicturesPage(page: Int) {
        view!!.showProgress()
        repository.downloadNewPage(page,new,popular)
    }

    override fun getAll() {
        disposable = repository.getAll(new,popular)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                view!!.onSuccess(response.filter { (it.new == new) && (it.popular == popular) })
            }
    }
    override fun onStop() {
        if(disposable != null)
            disposable!!.dispose()
        repository.onStop()
    }

    // Общение с репозиторием
    override fun onSuccess() {
        view!!.hideProgress()
        getAll()
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