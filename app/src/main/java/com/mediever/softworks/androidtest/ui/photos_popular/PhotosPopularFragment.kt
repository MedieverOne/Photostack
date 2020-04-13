package com.mediever.softworks.androidtest.ui.photos_popular

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mediever.softworks.androidtest.R
import com.mediever.softworks.androidtest.ui.BasePhotosListFragment
import com.mediever.softworks.androidtest.ui.adapters.PhotosAdapter
import com.mediever.softworks.androidtest.ui.photos_list_mvp.PhotosListPresenterImpl
import com.mediever.softworks.androidtest.util.Constants
import com.mediever.softworks.androidtest.util.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_photos_popular.view.*

class PhotosPopularFragment : BasePhotosListFragment() {

    private val popular = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_photos_popular, container, false)
        message = root.findViewById(R.id.no_internet_connection_popular)
        swipeRefreshLayout = root.findViewById(R.id.swipe_container_popular)
        recyclerView = root.findViewById(R.id.rv_photos_list_popular)
        presenter = PhotosListPresenterImpl(this, popular)
        swipeRefreshLayout.setOnRefreshListener(this)
        setupRecycler()
        changeState(Constants.FragmentState.INIT)
        return root
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
                loader = root.loader_popular
                loader.visibility = View.VISIBLE
                if (message.visibility == View.VISIBLE)
                    message.visibility = View.GONE
            }
            Constants.FragmentState.NEW_PAGE -> {
                loader = root.loader_page_popular
                loader.visibility = View.VISIBLE
            }
            Constants.FragmentState.BAD_CONNECT -> {
                message = root.no_internet_connection_popular
                message.visibility = View.VISIBLE
            }
            else -> {
            }
        }
    }
}