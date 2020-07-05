package com.ryccoatika.pictune.user.inventory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.adapter.UnsplashCollectionGridAdapter
import com.ryccoatika.pictune.adapter.UnsplashPhotoGridAdapter
import kotlinx.android.synthetic.main.fragment_unsplash_user_inventory.*

class UnsplashUserInventoryFragment : Fragment() {

    companion object {
        const val PARAM_USERNAME = "username"
        const val PARAM_MODE = "mode"

        const val MODE_PHOTOS = "photos"
        const val MODE_LIKES = "likes"
        const val MODE_COLLECTIONS = "collections"

        fun newInstance(username: String, mode: String): UnsplashUserInventoryFragment {
            val bundle = Bundle()
            bundle.putString(PARAM_USERNAME, username)
            bundle.putString(PARAM_MODE, mode)
            return UnsplashUserInventoryFragment().apply { arguments = bundle }
        }
    }

    private lateinit var username: String
    private lateinit var mode: String

    private val viewModel: UnsplashUserInventoryView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UnsplashUserInventoryViewModel::class.java)
    }
    private val photosAdapter: UnsplashPhotoGridAdapter by lazy {
        UnsplashPhotoGridAdapter()
    }
    private val likesAdapter: UnsplashPhotoGridAdapter by lazy {
        UnsplashPhotoGridAdapter()
    }
    private val collectionsAdapter: UnsplashCollectionGridAdapter by lazy {
        UnsplashCollectionGridAdapter()
    }

    private var currentPage = 1
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments?.getString(PARAM_USERNAME, "") ?: ""
        mode = arguments?.getString(PARAM_MODE, "") ?: MODE_PHOTOS
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unsplash_user_inventory, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UnsplashUserInventoryViewState.Loading -> {
                    showWarning(false)
                    if (!unsplash_user_inventory_swipe_refresh.isRefreshing)
                        unsplash_user_inventory_pb.visibility = View.VISIBLE
                }
                is UnsplashUserInventoryViewState.LoadMoreLoading -> {
                    when (mode) {
                        MODE_PHOTOS -> photosAdapter.showLoading()
                        MODE_COLLECTIONS -> collectionsAdapter.showLoading()
                        MODE_LIKES -> likesAdapter.showLoading()
                    }
                }
                is UnsplashUserInventoryViewState.SuccessPhotos -> {
                    showWarning(state.responses.isEmpty())
                    unsplash_user_inventory_pb.visibility = View.INVISIBLE
                    unsplash_user_inventory_swipe_refresh.isRefreshing = false
                    photosAdapter.photos = state.responses.toMutableList()

                    if (state.responses.isNotEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_user_inventory_rv.layoutManager as StaggeredGridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreUserPhotos(username, currentPage)
                        }
                        scrollListener?.let {
                            unsplash_user_inventory_rv.addOnScrollListener(it)
                        }
                    }
                }
                is UnsplashUserInventoryViewState.LoadMoreSuccessPhotos -> {
                    photosAdapter.hideLoading()
                    if (state.responses.isNotEmpty()) {
                        photosAdapter.loadMore(state.responses.toMutableList())
                        scrollListener?.isLoading = false
                        currentPage++
                    }
                }
                is UnsplashUserInventoryViewState.SuccessLikes -> {
                    showWarning(state.responses.isEmpty())
                    unsplash_user_inventory_pb.visibility = View.INVISIBLE
                    unsplash_user_inventory_swipe_refresh.isRefreshing = false
                    likesAdapter.photos = state.responses.toMutableList()

                    if (state.responses.isNotEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_user_inventory_rv.layoutManager as StaggeredGridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreUserLikes(username, currentPage)
                        }
                        scrollListener?.let {
                            unsplash_user_inventory_rv.addOnScrollListener(it)
                        }
                    }
                }
                is UnsplashUserInventoryViewState.LoadMoreSuccessLikes -> {
                    likesAdapter.hideLoading()
                    if (state.responses.isNotEmpty()) {
                        likesAdapter.loadMore(state.responses.toMutableList())
                        scrollListener?.isLoading = false
                        currentPage++
                    }
                }
                is UnsplashUserInventoryViewState.SuccessCollections -> {
                    showWarning(state.responses.isEmpty())
                    unsplash_user_inventory_pb.visibility = View.INVISIBLE
                    unsplash_user_inventory_swipe_refresh.isRefreshing = false
                    collectionsAdapter.collections = state.responses.toMutableList()

                    if (state.responses.isNotEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_user_inventory_rv.layoutManager as GridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreUserCollections(username, currentPage)
                        }
                        scrollListener?.let {
                            unsplash_user_inventory_rv.addOnScrollListener(it)
                        }
                    }
                }
                is UnsplashUserInventoryViewState.LoadMoreSuccessCollections -> {
                    collectionsAdapter.hideLoading()
                    if (state.responses.isNotEmpty()) {
                        collectionsAdapter.loadMore(state.responses.toMutableList())
                        scrollListener?.isLoading = false
                        currentPage++
                    }
                }
                is UnsplashUserInventoryViewState.Error -> {
                    showWarning(true)
                    unsplash_user_inventory_tv_warning.text = getString(R.string.text_failed_getting_data_try_refresh)
                    unsplash_user_inventory_pb.visibility = View.INVISIBLE
                    unsplash_user_inventory_swipe_refresh.isRefreshing = false
                    Log.w("190401", UnsplashUserInventoryFragment::class.simpleName, state.error)
                }
                is UnsplashUserInventoryViewState.LoadMoreError -> {
                    scrollListener?.isLoading = false
                    when (mode) {
                        MODE_PHOTOS -> photosAdapter.hideLoading()
                        MODE_COLLECTIONS -> collectionsAdapter.hideLoading()
                        MODE_LIKES -> likesAdapter.hideLoading()
                    }
                }
            }
        })

        unsplash_user_inventory_swipe_refresh.setOnRefreshListener {
            when (mode) {
                MODE_PHOTOS -> viewModel.getUserPhotos(username)
                MODE_LIKES -> viewModel.getUserLikes(username)
                MODE_COLLECTIONS -> viewModel.getUserCollections(username)
            }
        }

        when (mode) {
            MODE_PHOTOS -> {
                unsplash_user_inventory_rv.adapter = photosAdapter
                unsplash_user_inventory_rv.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                viewModel.getUserPhotos(username)
            }
            MODE_LIKES -> {
                unsplash_user_inventory_rv.adapter = likesAdapter
                unsplash_user_inventory_rv.layoutManager =
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                viewModel.getUserLikes(username)
            }
            MODE_COLLECTIONS -> {
                unsplash_user_inventory_rv.adapter = collectionsAdapter
                unsplash_user_inventory_rv.layoutManager = GridLayoutManager(requireContext(), 3)
                viewModel.getUserCollections(username)
            }
        }
    }

    private fun showWarning(state: Boolean) {
        if (state) {
            unsplash_user_inventory_rv.visibility = View.INVISIBLE
            unsplash_user_inventory_tv_warning.visibility = View.VISIBLE
            unsplash_user_inventory_tv_warning.text = getString(R.string.text_no_datas_found)
        } else {
            unsplash_user_inventory_tv_warning.visibility = View.VISIBLE
            unsplash_user_inventory_tv_warning.visibility = View.INVISIBLE
        }
    }

}
