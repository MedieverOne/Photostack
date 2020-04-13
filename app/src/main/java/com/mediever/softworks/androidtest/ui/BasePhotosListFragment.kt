package com.mediever.softworks.androidtest.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ListAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.agrawalsuneet.dotsloader.loaders.LinearDotsLoader
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.ui.adapters.PhotosAdapter
import com.mediever.softworks.androidtest.ui.photos_list_mvp.PhotosListContract
import com.mediever.softworks.androidtest.ui.photos_list_mvp.PhotosListPresenterImpl
import com.mediever.softworks.androidtest.util.Constants
import com.mediever.softworks.androidtest.util.GridItemDecoration
import com.mediever.softworks.androidtest.util.PicturesUtilCallback
import io.realm.Realm

abstract class BasePhotosListFragment : Fragment(), PhotosListContract.PhotosListView,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var gridLayoutManager: GridLayoutManager
    var presenter: PhotosListPresenterImpl? = null
    lateinit var recyclerView: RecyclerView
    lateinit var listAdapter: PhotosAdapter
    lateinit var root: View
    lateinit var loader: LinearDotsLoader
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var message: LinearLayout
    private val lastVisibleItemPosition: Int
        get() = gridLayoutManager.findLastVisibleItemPosition()
    var loading = true
    var state = Constants.FragmentState.INIT

    abstract fun setupRecycler()
    abstract override fun showProgress()

    protected val stateHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Log.d("HALO", "state: $state")
            when (state) {
                Constants.FragmentState.INIT -> {
                    loading = true; initScreen()
                }
                Constants.FragmentState.BAD_CONNECT -> {
                    loading = true; hideProgress(); badConnection()
                }
                Constants.FragmentState.NEW_PAGE -> {
                    loading = true; newPage()
                }
                Constants.FragmentState.LOADING -> {
                    loading = true
                }
                Constants.FragmentState.ACTIVE -> {
                    loading = false
                }
                Constants.FragmentState.UPDATE_DATA -> {
                    loading = true; updateData()
                }
                Constants.FragmentState.END_OF_DATA -> {
                    loading = true; hideProgress()
                }
            }
        }
    } // Handler

    protected fun setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (!loading && totalItemCount == (lastVisibleItemPosition + 1))
                    changeState(Constants.FragmentState.NEW_PAGE)
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recyclerView.removeItemDecorationAt(0)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.spanCount = 3
            recyclerView.addItemDecoration(GridItemDecoration(10, 3))
        } else {
            gridLayoutManager.spanCount = 2
            recyclerView.addItemDecoration(GridItemDecoration(10, 2))
        }
    }

    fun getPicturesPage() {
        changeState(Constants.FragmentState.LOADING)
        presenter!!.getPicturesPage()
    }

    protected fun initScreen() {
        listAdapter.clearData()
        showProgress()
        presenter!!.initPicturesByType()
    }

    protected fun updateData() {
        listAdapter.clearData()
        showProgress()
        presenter!!.updatePicturesByType()
    }

    protected fun badConnection() {
        showProgress()
    }

    protected fun newPage() {
        showProgress()
        getPicturesPage()
    }


    protected fun changeState(state: Constants.FragmentState) {
        this.state = state
        stateHandler.sendEmptyMessage(0)
    }

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
    }

    override fun hideProgress() {
        loader.visibility = View.GONE
    }

    override fun onSuccess(picturesList: List<Picture>) {
        if (picturesList.isNotEmpty()) {
            val diffUtilCallback: PicturesUtilCallback =
                PicturesUtilCallback(listAdapter.picturesList, picturesList)
            val picturesDiffResult = DiffUtil.calculateDiff(diffUtilCallback)
            listAdapter.setData(picturesList)
            picturesDiffResult.dispatchUpdatesTo(listAdapter)
            changeState(Constants.FragmentState.ACTIVE)
        } else {
            getPicturesPage()
        }
    }

    override fun onError() {
        hideProgress()
        changeState(Constants.FragmentState.BAD_CONNECT)
    }

    override fun endOfData() {
        changeState(Constants.FragmentState.END_OF_DATA)
    }

    override fun dataHasChanged(picturesList: List<Picture>) {
        if (picturesList.isEmpty()) {
            badConnection()
        } else {
            listAdapter.clearData()
            val diffUtilCallback: PicturesUtilCallback =
                PicturesUtilCallback(listAdapter.picturesList, picturesList)
            val picturesDiffResult = DiffUtil.calculateDiff(diffUtilCallback)
            listAdapter.setData(picturesList)
            picturesDiffResult.dispatchUpdatesTo(listAdapter)
            Log.d("HALO", "Pic List " + picturesList.size.toString())
        }
    }


    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = false
        if (state != Constants.FragmentState.LOADING
            && state != Constants.FragmentState.UPDATE_DATA
            && state != Constants.FragmentState.INIT
        ) {
            changeState(Constants.FragmentState.UPDATE_DATA)
        }
    }


}