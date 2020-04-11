package com.mediever.softworks.androidtest.database

import com.mediever.softworks.androidtest.models.Picture
import io.realm.Realm

class PicturesDatabase {
    fun addPicturesPage(page: List<Picture>) {
        Realm.getDefaultInstance().use { it.executeTransactionAsync { it.insertOrUpdate(page) } }
    }
    fun clearRealm(popular:Boolean) {
        Realm.getDefaultInstance().use {
            it.executeTransactionAsync {
                it.where(Picture::class.java)
                    .equalTo("popular",popular) // if(!popular) удаляем новые иначе популярные
                    .findAll().deleteAllFromRealm()
            }
        }
    }
    fun clearAll() {
        Realm.getDefaultInstance().use {
            it.executeTransactionAsync {
                it.where(Picture::class.java)
                    .findAll().deleteAllFromRealm()
            }
        }
    }

}