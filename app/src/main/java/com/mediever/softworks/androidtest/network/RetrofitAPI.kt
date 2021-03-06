package com.mediever.softworks.androidtest.network

import com.mediever.softworks.androidtest.models.PicturesList
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("photos")
    fun getPicturesPage(
        @Query("new") new: Boolean,
        @Query("popular") popular: Boolean,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Observable<Response<PicturesList>>
}

