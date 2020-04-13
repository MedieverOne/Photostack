package com.mediever.softworks.androidtest.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.util.Constants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photos_list_item.view.*
import java.lang.Exception

class PhotosAdapter(var onPicClick: ((Picture) -> Unit)? = null) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    var picturesList: ArrayList<Picture> = ArrayList()
    //var onPicClick: ((Picture) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotosViewHolder {
        return PhotosViewHolder(
            LayoutInflater
                .from(parent.context).inflate(R.layout.photos_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return picturesList.size
    }

//    fun addData(pictures: List<Picture>) {
//        this.picturesList.addAll(pictures)
//        notifyDataSetChanged()
//    }

    fun setData(pictures: List<Picture>) {
        this.picturesList = pictures as ArrayList<Picture>
        notifyDataSetChanged()
    }

    fun clearData() {
        this.picturesList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PhotosAdapter.PhotosViewHolder, position: Int) {
        val pic = picturesList[position]
        Picasso.get().load(Constants.IMAGE_URL + pic.image!!.name)
            .placeholder(R.drawable.loading_image)
            .fit().into(holder.picture, object : Callback {
                override fun onSuccess() {
                    holder.loader.visibility = View.INVISIBLE
                }

                override fun onError(e: Exception?) {
                    holder.loader.visibility = View.VISIBLE
                    Picasso.get().load(Constants.IMAGE_URL + pic.image!!.name)
                        .placeholder(R.drawable.loading_image).fit().into(holder.picture)
                }
            })
    }

    inner class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.photos_item_image
        val loader: ProgressBar = itemView.loader_item

        //val badInternet: LinearLayout = itemView.photos_item_no_internet
        init {
            itemView.setOnClickListener { onPicClick?.invoke(picturesList[adapterPosition]) }
        }
    }
}