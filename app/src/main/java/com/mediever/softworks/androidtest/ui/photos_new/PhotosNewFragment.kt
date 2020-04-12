package com.mediever.softworks.androidtest.ui.photos_new

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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


class PhotosNewFragment : Fragment(), PhotosListContract.PhotosListView , SwipeRefreshLayout.OnRefreshListener {

    lateinit var gridLayoutManager: GridLayoutManager
    var presenter:PhotosListPresenterImpl?  = null
    var state: Constants.FragmentState      = Constants.FragmentState.INIT
    var loading = false
    lateinit var recyclerView:RecyclerView
    lateinit var listAdapter:PhotosAdapter
    lateinit var root:View
    lateinit var loader:LinearDotsLoader
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var message : LinearLayout

    private val lastVisibleItemPosition: Int
        get() = gridLayoutManager.findLastVisibleItemPosition()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_photos_new, container, false)
        message            = root.findViewById(R.id.no_internet_connection_new)
        swipeRefreshLayout = root.findViewById(R.id.swipe_container_new)
        recyclerView       = root.findViewById(R.id.rv_photos_list_new)
        presenter          = PhotosListPresenterImpl(this,true,false)
        swipeRefreshLayout.setOnRefreshListener(this)
        setupRecycler()
        changeState(Constants.FragmentState.INIT)
        return root
    }

    /* *********************************************
                        SUPPORTS
    ************************************************ */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recyclerView.removeItemDecorationAt(0)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager.spanCount = 3
            recyclerView.addItemDecoration(GridItemDecoration(10,3))
        }
        else {
            gridLayoutManager.spanCount = 2
            recyclerView.addItemDecoration(GridItemDecoration(10,2))
        }
    }

    private fun setupRecycler() {
        listAdapter = PhotosAdapter()
        if(activity?.resources!!.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = GridLayoutManager(context, 3)
            recyclerView.layoutManager = gridLayoutManager
            recyclerView.addItemDecoration(GridItemDecoration(10,3))
        }
        else {
            gridLayoutManager = GridLayoutManager(context, 2)
            recyclerView.layoutManager = gridLayoutManager
            recyclerView.addItemDecoration(GridItemDecoration(10,2))
        }
        recyclerView.adapter = listAdapter
        setRecyclerViewScrollListener()
        listAdapter.onPicClick =  { picture ->
            val navController = findNavController()
            val args = Bundle()
            args.putInt(Constants.ARG_ID,picture.id!!)
            navController.navigate(R.id.action_photos_new_to_photo_info,args)
        }
    }

    private fun setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (!loading && totalItemCount == (lastVisibleItemPosition + 1)){
                    loading = true
                    changeState(Constants.FragmentState.NEW_PAGE)
                }
            }
        })
    }

    private fun getPicturesPage() {
        changeState(Constants.FragmentState.LOADING)
        presenter!!.getPicturesPage()
    }

    /* *********************************************
                    SCREEN STATE
    ************************************************ */

    private val stateHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (state) {
                Constants.FragmentState.INIT        -> { initScreen() }
                Constants.FragmentState.BAD_CONNECT -> { badConnection() }
                Constants.FragmentState.NEW_PAGE    -> { loading = true; newPage() }
                Constants.FragmentState.LOADING     -> { loading = true }
                Constants.FragmentState.ACTIVE      -> { loading = false }
                Constants.FragmentState.UPDATE_DATA -> { updateData() }
                Constants.FragmentState.END_OF_DATA -> { loading = true }
            }
        }
    } // Handler

    fun initScreen() {
        listAdapter.clearData()
        showProgress()
        presenter!!.initData()
    }

    fun updateData() {
        presenter!!.updateData()
        listAdapter.clearData()
        showProgress()
    }

    fun badConnection() { showProgress() }

    fun newPage() {
        loading = true
        showProgress()
        getPicturesPage()
    }


    fun changeState(state:Constants.FragmentState) {
        this.state = state
        stateHandler.sendEmptyMessage(0)
    }

    /* *********************************************
                CONTRACT REALIZATION
    ************************************************ */

    override fun onStop() {
        super.onStop()
        presenter!!.onStop()
    }

    override fun showProgress() {
        when (state) {
            Constants.FragmentState.INIT, Constants.FragmentState.UPDATE_DATA -> {
                loader = root.findViewById(R.id.loader_new)
                loader.visibility = View.VISIBLE
                if(message.visibility == View.VISIBLE)
                    message.visibility = View.GONE
            }
            Constants.FragmentState.NEW_PAGE -> {
                loader = root.findViewById(R.id.loader_page_new)
                loader.visibility = View.VISIBLE
            }
            Constants.FragmentState.BAD_CONNECT ->  {
                message = root.findViewById(R.id.no_internet_connection_new)
                message.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    override fun hideProgress() {
        loader.visibility = View.GONE
    }

    override fun onSuccess(picturesList: List<Picture>) {
        if(picturesList.isNotEmpty()) {
            val diffUtilCallback: PicturesUtilCallback =
                PicturesUtilCallback(listAdapter.mData, picturesList)
            val picturesDiffResult = DiffUtil.calculateDiff(diffUtilCallback)
            listAdapter.setData(picturesList)
            picturesDiffResult.dispatchUpdatesTo(listAdapter)
            changeState(Constants.FragmentState.ACTIVE)
        }else{
            getPicturesPage()
        }
    }

    override fun onError() {
        changeState(Constants.FragmentState.BAD_CONNECT)
    }

    override fun endOfData() {
        changeState(Constants.FragmentState.END_OF_DATA)
    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = false
        if(state != Constants.FragmentState.LOADING)
            changeState(Constants.FragmentState.UPDATE_DATA)
    }
}