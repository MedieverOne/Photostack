package com.mediever.softworks.androidtest.ui.photos_popular

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.ui.BasePhotosListFragment
import com.mediever.softworks.androidtest.ui.adapters.PhotosAdapter
import com.mediever.softworks.androidtest.ui.photos_list_mvp.PhotosListPresenterImpl
import com.mediever.softworks.androidtest.util.Constants
import com.mediever.softworks.androidtest.util.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_photos_popular.*
import kotlinx.android.synthetic.main.fragment_photos_popular.view.*

class PhotosPopularFragment : BasePhotosListFragment() {

    private val popular = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_photos_popular, container, false)
        recyclerView = root.rv_photos_list_popular
        setupRecycler()
        presenter = PhotosListPresenterImpl(this, popular)
        changeState(Constants.FragmentState.INIT)
        return root
    }

    override fun onResume() {
        super.onResume()
        swipe_container_popular.setOnRefreshListener(this)
    }

    override fun setupRecycler() {
        listAdapter = PhotosAdapter()
        if (activity?.resources!!.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = GridLayoutManager(context, 3)
            recyclerView.layoutManager = gridLayoutManager
            recyclerView.addItemDecoration(GridItemDecoration(10, 3))
        } else {
            gridLayoutManager = GridLayoutManager(context, 2)
            recyclerView.layoutManager = gridLayoutManager
            recyclerView.addItemDecoration(GridItemDecoration(10, 2))
        }
        recyclerView.adapter = listAdapter
        setRecyclerViewScrollListener()
        listAdapter.onPicClick = { picture ->
            val navController = findNavController()
            val args = Bundle()
            args.putInt(Constants.ARG_ID, picture.id!!)
            navController.navigate(R.id.action_photos_popular_to_photo_info, args)
        }
    }

    override fun showProgress() {
        when (state) {
            Constants.FragmentState.INIT, Constants.FragmentState.UPDATE_DATA -> {
                loader_popular.visibility = View.VISIBLE
                if (no_internet_connection_popular.visibility == View.VISIBLE)
                    no_internet_connection_popular.visibility = View.GONE
            }
            Constants.FragmentState.NEW_PAGE -> {
                loader_page_popular.visibility = View.VISIBLE
            }
            Constants.FragmentState.BAD_CONNECT -> {
                no_internet_connection_popular.visibility = View.VISIBLE
            }
            else -> {
            }
        }
    }

    override fun hideProgress() {
        if (loader_page_popular.isVisible)
            loader_page_popular.visibility = View.GONE
        if (loader_popular.isVisible)
            loader_popular.visibility = View.GONE
    }

    override fun onRefresh() {
        swipe_container_popular.isRefreshing = false
        super.onRefresh()
    }
}