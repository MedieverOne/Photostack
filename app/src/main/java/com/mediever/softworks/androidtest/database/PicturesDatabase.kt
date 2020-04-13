package com.mediever.softworks.androidtest.database

import android.util.Log
import com.mediever.softworks.androidtest.models.Picture
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults

class PicturesDatabase {
    fun addPicturesPage(picDataModel: PicPageDataModel) {
        Realm.getDefaultInstance()
            .use { it -> it.executeTransactionAsync { it.insertOrUpdate(picDataModel) } }
    }

//    fun clearAllPagesByType(popular: Boolean) {
//        Realm.getDefaultInstance().use { it ->
//            it.executeTransaction {
//                val result: RealmResults<PicPageDataModel>? = it.where(PicPageDataModel::class.java)
//                    .equalTo("data.popular", popular).findAll()
//                if (result != null) {
//                    result.deleteAllFromRealm()
//                }
//            }
//        }
//    }

    fun clearAll() {
        Realm.getDefaultInstance().use { it ->
            Log.d("HALO", "CLEAR ALL")
            it.executeTransactionAsync {
                it.where(PicPageDataModel::class.java).realm.deleteAll()
            }
        }
    }
}