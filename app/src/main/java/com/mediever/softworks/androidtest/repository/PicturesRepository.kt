package com.mediever.softworks.androidtest.repository

import android.util.Log
import com.mediever.softworks.androidtest.models.PicPageDataModel
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.network.ApiClient
import com.mediever.softworks.androidtest.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.*

class PicturesRepository(private val presenter: RepositoryContract.PicturesPresenterContract) :
    RepositoryContract.PicturesRepositoryContract {
    private var disposable: Disposable = CompositeDisposable()
    private val realm: Realm = Realm.getDefaultInstance()
    private var page: Int = 1
    private val realmListener = RealmChangeListener<Realm> { realm ->
        presenter.dataHasChanged(realm.where(Picture::class.java).findAll())
    }


    override fun downloadNewPage(popular: Boolean) {
        realm.addChangeListener(realmListener)
        val head = realm.where(PicPageDataModel::class.java)
            .equalTo("type", popular)
            .findFirst()
        Log.d("HALO", "in dNP" + head.toString())
        if (head != null && head.countOfPages > page) {
            val tail = realm.where(PicPageDataModel::class.java)
                .equalTo("type", popular)
                .findAll()
                .last()
            page = tail!!.page + 1
            downloadPage(popular, page)
        } else if (head == null) {
            page = 1
            downloadPage(popular, page)
        } else {
            presenter.endOfData()
        }

    }

    override fun getAllByType(popular: Boolean): Observable<List<Picture>> =
        Observable.just(realm.where(Picture::class.java).findAll())

    override fun initPicturesByType(popular: Boolean) {
        if (realm.where(PicPageDataModel::class.java).findFirst() != null)
            presenter.onSuccess()
        else
            downloadNewPage(popular)
    }

    override fun updatePicturesByType(popular: Boolean) {
        Realm.getDefaultInstance().executeTransaction {
            Realm.getDefaultInstance().where(PicPageDataModel::class.java).realm.deleteAll()
        }
        page = 1
        downloadNewPage(popular)
    }

    override fun onStop() {
        disposable.dispose()
    }


    fun downloadPage(popular: Boolean, page: Int) {
        disposable = ApiClient.instance
            .getPicturesPage(true, popular, page, Constants.LIMIT_PER_PAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                when (response.code()) {
                    200, 201, 202 -> {
                        if (response.body()!!.data.isEmpty())
                            presenter.endOfData()
                        else {
                            val picPage =
                                PicPageDataModel()
                            picPage.data.addAll(response.body()!!.data)
                            picPage.countOfPages = response.body()!!.countOfPages
                            picPage.page = page
                            picPage.type = popular
                            this.page++
                            addPageToDatabase(picPage)
                            presenter.onSuccess()
                        }
                    }
                }
            }, { _ ->
                if (realm.where(PicPageDataModel::class.java).findFirst() == null)
                    presenter.emptyRepository()
                else
                    presenter.onFailure()
            })
    }

    private fun addPageToDatabase(picDataModel: PicPageDataModel) {
        Realm.getDefaultInstance()
            .use { it -> it.executeTransactionAsync { it.insertOrUpdate(picDataModel) } }
    }
}