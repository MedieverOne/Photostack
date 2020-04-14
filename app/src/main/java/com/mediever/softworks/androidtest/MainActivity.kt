package com.mediever.softworks.androidtest

import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mediever.softworks.androidtest.models.PicPageDataModel
import io.realm.Realm


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isOnline()) {
            Realm.getDefaultInstance().executeTransaction {
                Realm.getDefaultInstance().where(PicPageDataModel::class.java).realm.deleteAll()
            }
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        Realm.getDefaultInstance().close()
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(): Boolean {
        val cm =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null
    }
}
