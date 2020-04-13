package com.mediever.softworks.androidtest.util

object Constants {
    const val BASE_URL = "http://gallery.dev.webant.ru/api/"
    const val IMAGE_URL = "http://gallery.dev.webant.ru/media/"
    const val LIMIT_PER_PAGE = 10
    const val ARG_ID = "id"
    const val REALM_NAME = "pictures_db"

    // Состояния фрагмента
    enum class FragmentState {
        INIT, UPDATE_DATA, LOADING, BAD_CONNECT, ACTIVE, NEW_PAGE, END_OF_DATA
    }
}