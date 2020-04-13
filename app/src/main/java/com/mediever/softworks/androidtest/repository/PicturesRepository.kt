package com.mediever.softworks.androidtest.repository

import android.util.Log
import androidx.annotation.NonNull
import com.mediever.softworks.androidtest.database.PicturesDatabase
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.network.ApiClient
import com.mediever.softworks.androidtest.util.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.*

class PicturesRepository(val presenter:RepositoryContract.PicturesPresenterContract) : RepositoryContract.PicturesRepositoryContract {
    @NonNull
    var disposable: Disposable? = null
    val realm:Realm = Realm.getDefaultInstance()
    private val database: PicturesDatabase = PicturesDatabase()
    var prevPage: Int = 0 // предотвращение повторной загрузки
    var page: Int = 1

    private fun addPage(pictures: List<Picture>) = database.addPicturesPage(pictures)

    override fun downloadNewPage(new: Boolean, popular: Boolean) {
        // проверка на популярность и то была ли загружена страница в БД
        if( ((popular && page > Constants.totalPopularPages)
            || (!popular && page > Constants.totalNewPages)) && prevPage != page) {
            disposable = ApiClient.instance
                .getPicturesPage(new, popular, page, Constants.LIMIT_PER_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    when (response.code()) {
                        200, 201, 202 -> {
                            if(response.body()!!.data.isEmpty())
                                presenter.endOfData()
                            else {
                                addPage(response.body()!!.data)
                                if (popular)
                                    Constants.totalPopularPages++
                                else
                                    Constants.totalNewPages++
                                prevPage = page
                                page++
                                presenter.onSuccess()
                            }
                        }
                        401 -> {
                        }
                        402 -> {
                        }
                        500 -> {
                        }
                        501 -> {
                        }
                    }
                }, { _ ->
                    // проверка на наличие страниц(если не загрузилась даже первая страница - показываем отсутствие подключения)
                    if(page < 2)
                        presenter.emptyRepository()
                    else
                        presenter.onFailure()
                })
        }else
            presenter.onSuccess()
    }

    override fun getAll(new: Boolean, popular: Boolean): Observable<List<Picture>>
            = Observable.just(realm.where(Picture::class.java).findAll())

    override fun initData(popular:Boolean) {
        page = if(popular) Constants.totalPopularPages + 1 else Constants.totalNewPages + 1
        prevPage = page-1
        if(page != 1)
            presenter.onSuccess()
        else
            downloadNewPage(true,popular)
    }

    override fun updateData(popular:Boolean) {
        page = 1
        prevPage = 0
        Constants.totalNewPages = 0
        Constants.totalPopularPages = 0
        database.clearRealm(popular)
        downloadNewPage(true,popular)
    }

    override fun onStop() {
        if(disposable != null)
            disposable!!.dispose()
    }
}