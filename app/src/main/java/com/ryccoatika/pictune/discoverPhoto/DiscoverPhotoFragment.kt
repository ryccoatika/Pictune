package com.ryccoatika.pictune.discoverPhoto

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.*
import com.ryccoatika.core.domain.repository.DiscoverPhotoOrder
import com.ryccoatika.pictune.R
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.core.ui.PhotoAdapter
import kotlinx.android.synthetic.main.fragment_discover_photos.*
import kotlinx.android.synthetic.main.view_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverPhotoFragment : Fragment() {

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private val viewModel: DiscoverPhotoViewModel by viewModel()

    private var currentPage = 1
    private var orderBy = DiscoverPhotoOrder.LATEST
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        // setting staggered grid layout to not blinking and change position
        photoAdapter.setHasStableIds(true)

        with(rv_photos) {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = photoAdapter
        }

        viewModel.viewState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DiscoverPhotoViewState.Error -> {
                    swipe_refresh.isRefreshing = false
                    rv_photos.isVisible = false
                    view_loading.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = true
                    tv_error.text = result.message ?: getString(R.string.something_went_wrong)
                    scrollListener?.isLoading = false
                    photoAdapter.hideLoading()
                }
                // discover
                is DiscoverPhotoViewState.DiscoverLoading -> {
                    if (!swipe_refresh.isRefreshing)
                        view_loading.isVisible = true
                    rv_photos.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = false
                    currentPage = 1
                }
                is DiscoverPhotoViewState.DiscoverEmpty -> {
                    swipe_refresh.isRefreshing = false
                    rv_photos.isVisible = false
                    view_loading.isVisible = false
                    view_empty.isVisible = true
                    view_error.isVisible = false
                }
                is DiscoverPhotoViewState.DiscoverSuccess -> {
                    swipe_refresh.isRefreshing = false
                    rv_photos.isVisible = true
                    view_loading.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = false
                    photoAdapter.setPhotos(result.data)
                    currentPage++
                    scrollListener = RecycleViewLoadMoreScroll(
                        rv_photos.layoutManager as StaggeredGridLayoutManager
                    )
                    scrollListener?.let {
                        it.setOnLoadMoreListener {
                            viewModel.loadMorePhoto(orderBy.value, currentPage)
                        }
                        rv_photos.addOnScrollListener(it)
                    }
                }
                // loadmore
                is DiscoverPhotoViewState.LoadMoreLoading -> {
                    photoAdapter.showLoading()
                }
                is DiscoverPhotoViewState.LoadMoreEmpty -> {

                }
                is DiscoverPhotoViewState.LoadMoreSuccess -> {
                    photoAdapter.hideLoading()
                    photoAdapter.insertPhotos(result.data)
                    scrollListener?.isLoading = false
                    currentPage++
                }
            }
        }

        swipe_refresh.setOnRefreshListener {
            viewModel.discoverPhoto(orderBy.value)
        }

        viewModel.discoverPhoto(orderBy.value)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.photos_by_latest -> {
                orderBy = DiscoverPhotoOrder.LATEST
                viewModel.discoverPhoto(orderBy.value)
                true
            }
            R.id.photos_by_popular -> {
                orderBy = DiscoverPhotoOrder.POPULAR
                viewModel.discoverPhoto(orderBy.value)
                true
            }
            R.id.photos_by_oldest -> {
                orderBy = DiscoverPhotoOrder.OLDEST
                viewModel.discoverPhoto(orderBy.value)
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.discover_photos_filter, menu)
    }
}
