package com.ryccoatika.pictune.discoverPhotos

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.adapter.UnsplashPhotoGridAdapter
import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import kotlinx.android.synthetic.main.fragment_unsplash_discover_photos.*

class UnsplashDiscoverPhotosFragment : Fragment() {

    companion object {
        private const val LATEST_PHOTOS = UnsplashPhotoResponse.ORDER_BY_LATEST
        private const val POPULAR_PHOTOS = UnsplashPhotoResponse.ORDER_BY_POPULAR
        private const val OLDEST_PHOTOS = UnsplashPhotoResponse.ORDER_BY_OLDEST
    }

    private var currentPage = 1
    private var orderBy = LATEST_PHOTOS
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    private val gridAdapter: UnsplashPhotoGridAdapter by lazy {
        UnsplashPhotoGridAdapter()
    }

    private val viewModel: UnsplashDiscoverPhotosView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UnsplashDiscoverPhotosViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unsplash_discover_photos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // setting staggered grid layout to not blinking and change position
        gridAdapter.setHasStableIds(true)

        unsplash_discover_photos_rv.setHasFixedSize(true)
        unsplash_discover_photos_rv.adapter = gridAdapter

        // observer view model state for changes
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UnsplashDiscoverPhotosViewState.DiscoverLoading -> {
                    showWarning(false)
                    if(!unsplash_discover_photos_swipe_refresh.isRefreshing)
                        unsplash_discover_photos_pb.visibility = View.VISIBLE
                }
                is UnsplashDiscoverPhotosViewState.LoadMoreLoading -> {
                    gridAdapter.showLoading()
                }
                is UnsplashDiscoverPhotosViewState.DiscoverSuccess -> {
                    showWarning(state.responses.isEmpty())
                    unsplash_discover_photos_swipe_refresh.isRefreshing = false
                    unsplash_discover_photos_pb.visibility = View.INVISIBLE
                    gridAdapter.photos = state.responses.toMutableList()

                    if (state.responses.isNotEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_discover_photos_rv.layoutManager as StaggeredGridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.unsplashLoadMore(currentPage, orderBy)
                        }
                        scrollListener?.let {
                            unsplash_discover_photos_rv.addOnScrollListener(it)
                        }
                    }
                }
                is UnsplashDiscoverPhotosViewState.LoadMoreSuccess -> {
                    gridAdapter.hideLoading()
                    if (state.responses.isNotEmpty()) {
                        gridAdapter.loadMore(state.responses.toMutableList())
                        scrollListener?.isLoading = false
                        currentPage++
                    }
                }
                is UnsplashDiscoverPhotosViewState.LoadMoreError -> {
                    scrollListener?.isLoading = false
                    gridAdapter.hideLoading()
                }
                is UnsplashDiscoverPhotosViewState.DiscoverError -> {
                    showWarning(true)
                    unsplash_discover_photos_swipe_refresh.isRefreshing = false
                    unsplash_discover_photos_pb.visibility = View.INVISIBLE
                    unsplash_discover_photos_tv_warning.text = getString(R.string.text_failed_getting_data_try_refresh)
                    Log.w("190401", UnsplashDiscoverPhotosFragment::class.simpleName, state.error)
                }
            }
        })

        unsplash_discover_photos_swipe_refresh.setOnRefreshListener {
            viewModel.unsplashDiscover(orderBy)
        }

        viewModel.unsplashDiscover()
    }

    private fun showWarning(state: Boolean) {
        if (state) {
            unsplash_discover_photos_rv.visibility = View.INVISIBLE
            unsplash_discover_photos_tv_warning.visibility = View.VISIBLE
            unsplash_discover_photos_tv_warning.text = getString(R.string.text_no_datas_found)
        } else {
            unsplash_discover_photos_rv.visibility = View.VISIBLE
            unsplash_discover_photos_tv_warning.visibility = View.INVISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.photos_by_latest -> {
                orderBy = LATEST_PHOTOS
                viewModel.unsplashDiscover(orderBy)
                true
            }
            R.id.photos_by_popular -> {
                orderBy = POPULAR_PHOTOS
                viewModel.unsplashDiscover(orderBy)
                true
            }
            R.id.photos_by_oldest -> {
                orderBy = OLDEST_PHOTOS
                viewModel.unsplashDiscover(orderBy)
                true
            }
            else -> false
        }
    }
}
