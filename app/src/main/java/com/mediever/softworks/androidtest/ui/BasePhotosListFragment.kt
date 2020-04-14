package com.mediever.softworks.androidtest.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.models.Picture
import com.mediever.softworks.androidtest.ui.adapters.PhotosAdapter
import com.mediever.softworks.androidtest.ui.photos_list_mvp.PhotosListContract
import com.mediever.softworks.androidtest.ui.photos_list_mvp.PhotosListPresenterImpl
import com.mediever.softworks.androidtest.util.Constants
import com.mediever.softworks.androidtest.util.GridItemDecoration
import com.mediever.softworks.androidtest.util.PicturesUtilCallback

abstract class BasePhotosListFragment : Fragment(), PhotosListContract.PhotosListView,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var gridLayoutManager: GridLayoutManager
    protected var presenter: PhotosListPresenterImpl? = null
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var listAdapter: PhotosAdapter
    private val lastVisibleItemPosition: Int
        get() = gridLayoutManager.findLastVisibleItemPosition()
    protected var loading = true
    protected var state = Constants.FragmentState.INIT

    abstract fun setupRecycler()
    abstract override fun showProgress()
    abstract override fun hideProgress()

    private val stateHandler = @SuppressLint("HandlerLeak")
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
    }

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

    private fun getPicturesPage() {
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

    override fun onSuccess(picturesList: List<Picture>) {
        if (picturesList.isNotEmpty()) {
            val diffUtilCallback = PicturesUtilCallback(listAdapter.picturesList, picturesList)
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
            val diffUtilCallback = PicturesUtilCallback(listAdapter.picturesList, picturesList)
            val picturesDiffResult = DiffUtil.calculateDiff(diffUtilCallback)
            listAdapter.setData(picturesList)
            picturesDiffResult.dispatchUpdatesTo(listAdapter)
        }
    }


    override fun onRefresh() {
        if(isOnline()) {
            if (state != Constants.FragmentState.LOADING
                && state != Constants.FragmentState.UPDATE_DATA
                && state != Constants.FragmentState.INIT
            ) {
                changeState(Constants.FragmentState.UPDATE_DATA)
            }
        }else{
            Toast.makeText(context, R.string.bad_internet,Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(): Boolean {
        val cm =
            context!!.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null
    }

}