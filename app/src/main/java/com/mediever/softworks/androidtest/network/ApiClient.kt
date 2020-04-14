package com.mediever.softworks.androidtest.network

import com.google.gson.GsonBuilder
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.models.PicturesList
import com.mediever.softworks.androidtest.util.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private val retrofitAPI: RetrofitAPI

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val clientBuilder = OkHttpClient.Builder().addInterceptor(loggingInterceptor)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(clientBuilder.build())
            .build()

        retrofitAPI = retrofit.create(RetrofitAPI::class.java)
    }

    companion object {
        private var apiClient: ApiClient? = null
        val instance: ApiClient
            get() {
                if (apiClient == null) {
                    apiClient = ApiClient()
                }
                return apiClient as ApiClient
            }
    }

    fun getPicturesPage(
        new: Boolean,
        popular: Boolean,
        page: Int,
        limit: Int
    ): Observable<Response<PicturesList>> {
        return retrofitAPI.getPicturesPage(new, popular, page, limit)
    }
}