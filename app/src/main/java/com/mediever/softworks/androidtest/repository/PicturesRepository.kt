package com.mediever.softworks.androidtest.repository

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

    private fun addPage(pictures: List<Picture>) = database.addPicturesPage(pictures)

    override fun downloadNewPage(page: Int, new: Boolean, popular: Boolean) {
        // проверка на популярность и то была ли загружена страница в БД
        if( (popular && page > Constants.totalPopularPages)
            || (!popular && page > Constants.totalNewPages)) {
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


    override fun updateData(popular:Boolean) {
        Constants.totalNewPages = 0
        Constants.totalPopularPages = 0
        downloadNewPage(1,true,popular)
        database.clearRealm(popular)
    }

    override fun onStop() {
        if(disposable != null)
            disposable!!.dispose()
    }
}