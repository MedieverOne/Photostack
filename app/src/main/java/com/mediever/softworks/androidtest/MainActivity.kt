package com.mediever.softworks.androidtest

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mediever.softworks.androidtest.database.PicPageDataModel
import com.mediever.softworks.androidtest.database.PicturesDatabase
import io.realm.Realm

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val database = PicturesDatabase()
//        database.clearAll()

        Realm.getDefaultInstance().executeTransaction {
            Realm.getDefaultInstance().where(PicPageDataModel::class.java).realm.deleteAll()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        Realm.getDefaultInstance().close()
        super.onDestroy()
    }


}
