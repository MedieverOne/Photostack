package com.mediever.softworks.androidtest.ui.photo_info

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.util.Constants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_photo_info.*
import java.lang.Exception

class PhotoInfoActivity : AppCompatActivity(), PhotoInfoContract.PhotoInfoView {
    private var presenter: PhotoInfoPresenterImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_info)
        presenter = PhotoInfoPresenterImpl(this)
        initScreen()
    }

    override fun onResume() {
        super.onResume()
        initScreen()
    }

    private fun initScreen() {
        presenter!!.getPicture(intent.getIntExtra(Constants.ARG_ID, 0))
        toolbar_photo_info.setNavigationOnClickListener { onBackPressed() }
        setImageHeight(this.resources!!.configuration.orientation)
    }

    private fun setImageHeight(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            iv_photo_info.layoutParams.height = windowManager.defaultDisplay.height
        else
            iv_photo_info.layoutParams.height =
                resources.getDimension(R.dimen.photo_info_height_portrait).toInt()
    }

    override fun onSuccess(picture: Picture?) {
        tv_title_photo_info.text = picture!!.name
        tv_text_photo_info.text = picture.description
        Picasso.get().load(Constants.IMAGE_URL + picture.image!!.name)
            .placeholder(R.drawable.loading_image).fit()
            .into(iv_photo_info, object : Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    badConnect()
                }
            })
    }

    override fun onError(throwable: Throwable) = badConnect()

    private fun badConnect() = iv_photo_info.setImageResource(R.drawable.no_internet)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setImageHeight(newConfig.orientation)
    }

    override fun onStop() {
        presenter!!.onStop()
        super.onStop()
    }
}