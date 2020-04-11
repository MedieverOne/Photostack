package com.mediever.softworks.androidtest.util

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class PicStackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name(Constants.REALM_NAME)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}