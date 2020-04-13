package com.mediever.softworks.androidtest.ui.photo_info

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.util.Constants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class PhotoInfoActivity : AppCompatActivity(), PhotoInfoContract.PhotoInfoView {

    lateinit var imageView: ImageView
    lateinit var title: TextView
    lateinit var text: TextView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    var presenter: PhotoInfoPresenterImpl? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_info)
        imageView = findViewById(R.id.iv_photo_info)
        title = findViewById(R.id.tv_title_photo_info)
        text = findViewById(R.id.tv_text_photo_info)
        toolbar = findViewById(R.id.toolbar_photo_info)
        presenter = PhotoInfoPresenterImpl(this)
        initScreen()
    }

    private fun initScreen() {
        presenter!!.getPicture(intent.getIntExtra(Constants.ARG_ID, 0))
        toolbar.setNavigationOnClickListener { onBackPressed() }
        setImageHeight(this.resources!!.configuration.orientation)
    }

    private fun setImageHeight(orientation: Int) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            imageView.layoutParams.height = windowManager.defaultDisplay.height
        else
            imageView.layoutParams.height =
                resources.getDimension(R.dimen.photo_info_height_portrait).toInt()
    }

    override fun onSuccess(picture: Picture?) {
        title.text = picture!!.name
        text.text = picture.description
        Picasso.get().load(Constants.IMAGE_URL + picture.image!!.name)
            .placeholder(R.drawable.loading_image).fit()
            .into(imageView, object : Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    badConnect()
                }
            })
    }

    override fun onError(throwable: Throwable) = badConnect()

    private fun badConnect() = imageView.setImageResource(R.drawable.no_internet)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setImageHeight(newConfig.orientation)
    }
}