package com.mediever.softworks.androidtest.util

import androidx.recyclerview.widget.DiffUtil
import com.mediever.softworks.androidtest.models.Picture

class PicturesUtilCallback(
    private val oldList: List<Picture>,
    private val newList: List<Picture>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPicture = oldList[oldItemPosition]
        val newPicture = newList[newItemPosition]
        return oldPicture.id == newPicture.id
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }

}